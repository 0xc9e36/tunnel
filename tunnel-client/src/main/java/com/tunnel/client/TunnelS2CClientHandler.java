package com.tunnel.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.Constant;
import com.tunnel.common.HttpData;
import com.tunnel.common.HttpUtil;
import com.tunnel.common.TunnelBaseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class TunnelS2CClientHandler extends TunnelBaseHandler {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private TunnelS2CClient tunnelS2CClient;
	
	public TunnelS2CClientHandler(TunnelS2CClient tunnelS2CClient) {
		super("S2C");
		this.tunnelS2CClient = tunnelS2CClient;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		//注册
		String localHost = "...";
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {}
		String content = Config.NAME + "[" + localHost + "]" + Constant.SPLIT_FLAG + Config.HOST_ARY;
		sendCommonData(ctx, content.getBytes());
	}

	@Override
	protected void handleData(ChannelHandlerContext ctx, ByteBuf buf, byte flag) {
		if(flag == COMMON_MSG){
			doHttpRequest(ctx, buf);
		}else{
			doRegisterResult(ctx, buf);
		}
	}

	public void doRegisterResult(ChannelHandlerContext ctx, ByteBuf buf){
		if(LOGGER.isInfoEnabled()){
			byte[] content = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), content,0,content.length);
			LOGGER.info(new String(content));
		}
	}
	
	public void doHttpRequest(ChannelHandlerContext ctx, ByteBuf buf){
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
			
			//发送回到服务端
			ChannelHandlerContext c2sCtx = TunnelC2SManager.getChannelHandlerContext();
			if(c2sCtx != null){
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
							sendCommonData(c2sCtx, requestidBytes, readData.getHeader(), readData.getData());
						} else {
							byte[] response404 = HttpUtil.response404("tunnel-client");
							sendCommonData(c2sCtx, requestidBytes, response404);
						}
						out.close();
					} catch (Exception e) {
						byte[] response500 = HttpUtil.response500(e.getMessage(),"tunnel-client");
						sendCommonData(c2sCtx, requestidBytes, response500);
					} finally {
						try {
							socket.close();
						} catch (Exception e2) {}
					}
				}else{
					byte[] response404 = HttpUtil.response404("tunnel-client");
					sendCommonData(c2sCtx, requestidBytes, response404);
				}
			}
		}
	}
	
	@Override
	protected void handleWriterIdle(ChannelHandlerContext ctx) {
		//心跳
		super.handleWriterIdle(ctx);
		sendPingMsg(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//断线重连
		super.channelInactive(ctx);
		tunnelS2CClient.connect();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		LOGGER.error("",e);
		ctx.close();
	}
}