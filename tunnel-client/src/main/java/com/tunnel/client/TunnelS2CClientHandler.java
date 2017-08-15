package com.tunnel.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.tunnel.common.Constant;
import com.tunnel.common.HttpData;
import com.tunnel.common.HttpUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

public class TunnelS2CClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		//注册
		String localHost = "...";
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {}
		String content = Config.NAME + "[" + localHost + "]" + Constant.SPLIT_FLAG + Config.HOST_ARY;
		ctx.writeAndFlush(content+Constant.ENT_FLAG);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		//前17个字符是
		//host_index(在Config.HOST_LIST里的下标) 占1位
		//requestid 时间戳+三位随机数，代表http请求的编号 占16位
		if(buf.readableBytes() > 17){
			byte[] hostIndexBytes = new byte[1];
			buf.getBytes(buf.readerIndex(), hostIndexBytes, 0, 1);
			
			byte[] requestidBytes = new byte[16];
			buf.getBytes(buf.readerIndex()+1, requestidBytes, 0, 16);
			
			byte[] dataBytes = new byte[buf.readableBytes()-1-16];
			buf.getBytes(buf.readerIndex()+1+16, dataBytes, 0, dataBytes.length);
			
			//解析完数据接口
			//开始查找转发地址，转发数据
			int hostIndex = Integer.parseInt(new String(hostIndexBytes));
			if(hostIndex < Config.HOST_LIST.size()){
				IpAndPort ipAndPort = Config.HOST_LIST.get(hostIndex);
				
				Socket socket = new Socket();
				try {
					socket.connect(new InetSocketAddress(ipAndPort.getIp(), ipAndPort.getPort()), 1000);//设置连接请求超时时间10 s
					socket.setSoTimeout(30000);//设置读操作超时时间30 s
					
					OutputStream out = socket.getOutputStream();
					out.write(dataBytes);
					out.flush();
					
					InputStream in = socket.getInputStream();
					HttpData readData = HttpUtil.readData(in);
					if(readData != null){
						//发送回到服务端
						ChannelHandlerContext c2sCtx = TunnelC2SManager.getChannelHandlerContext();
						if(c2sCtx != null){
							byte[] endFlagBytes = Constant.ENT_FLAG.getBytes();
							ByteBuf dispatchBuf = Unpooled.buffer(requestidBytes.length+readData.getHeader().length+readData.getData().length+endFlagBytes.length);  
				        	dispatchBuf.writeBytes(requestidBytes);
				        	dispatchBuf.writeBytes(readData.getHeader());  
				        	dispatchBuf.writeBytes(readData.getData());
				        	dispatchBuf.writeBytes(endFlagBytes);
							
							c2sCtx.writeAndFlush(dispatchBuf);
						}
					} else {
						FullHttpResponse response404 = HttpUtil.response404();
						ctx.writeAndFlush(response404).addListener(ChannelFutureListener.CLOSE);
					}
					out.close();
				} catch (Exception e) {
					FullHttpResponse response500 = HttpUtil.response500(e.getMessage());
					ctx.writeAndFlush(response500).addListener(ChannelFutureListener.CLOSE);
				} finally {
					try {
						socket.close();
					} catch (Exception e2) {}
				}
			}else{
				FullHttpResponse response404 = HttpUtil.response404();
				ctx.writeAndFlush(response404).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

}