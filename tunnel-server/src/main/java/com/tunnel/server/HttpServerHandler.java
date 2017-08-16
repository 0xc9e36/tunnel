package com.tunnel.server;
  
import java.io.ByteArrayOutputStream;

import com.tunnel.common.Constant;
import com.tunnel.common.HttpData;
import com.tunnel.common.HttpUtil;
import com.tunnel.common.StringUtil;
import com.tunnel.common.TunnelBaseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;  
  
public class HttpServerHandler extends SimpleChannelInboundHandler<ByteBuf>{  
    public static final AttributeKey<String> HTTP_REQUESTID_KEY = AttributeKey.valueOf("http.requestid"); 
    ByteArrayOutputStream baos = null;
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
    	cause.printStackTrace();  
		removeHttp(ctx);
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		
		byte[] data = new byte[buf.readableBytes()];
		buf.getBytes(buf.readerIndex(), data);
	
		//数据中可能包含多个数据包
		//每个数据包都以结束符作为结束
		if(baos != null){
			baos.write(data, 0, data.length);
			data = baos.toByteArray();
		}
		HttpData httpData = HttpUtil.analyzeHttpData(data);
		if(httpData != null){
			if(!httpData.isOk()){
				//说明还没有接收完，继续接收
				if(baos == null){
					baos = new ByteArrayOutputStream();
					baos.write(data, 0, data.length);
				}
			}else{
				//释放缓冲
				try {
					if(baos != null){
						baos.close();
						baos = null;
					}
				} catch (Exception e) {}
				
				String host = HttpUtil.getHost(httpData.getHeader());
				host = host==null?"":host.trim();
				
				Tunnel tunnel = TunnelManager.get(host);
				if(tunnel != null){
					ChannelHandlerContext clientCtx = tunnel.getChannelHandlerContext();
					Attribute<String> attr = ctx.attr(HTTP_REQUESTID_KEY);
					String requestid = System.currentTimeMillis()+StringUtil.getRandomString(3);
//					Attribute<String> attr = ctx.attr(HTTP_REQUESTID_KEY);
					if(attr.get() == null){
						attr.setIfAbsent(requestid);
					}
					HttpChannelManager.add(requestid, ctx);
					super.channelActive(ctx);
					if(attr.get() != null){
						byte[] hostIndexBytes = String.valueOf(tunnel.getHostIndex()).getBytes();
						byte[] requestIdBytes = attr.get().getBytes();
						ByteBuf dispatchBuf = Unpooled.buffer(hostIndexBytes.length+requestIdBytes.length+data.length+Constant.ENT_FLAG_BYTES.length);  
						dispatchBuf.writeByte(TunnelBaseHandler.COMMON_MSG);//标识
						dispatchBuf.writeBytes(hostIndexBytes);//数据
			        	dispatchBuf.writeBytes(requestIdBytes); //数据 
			        	dispatchBuf.writeBytes(data);//数据
			        	dispatchBuf.writeBytes(Constant.ENT_FLAG_BYTES);//结束标识
			        	clientCtx.writeAndFlush(dispatchBuf);
					}
					
				}else{
					byte[] response404 = HttpUtil.response404("tunnel-server");
					ctx.writeAndFlush(response404).addListener(ChannelFutureListener.CLOSE);
				}
			}
			
		}else{
			byte[] response400 = HttpUtil.response400("tunnel-server");
			ctx.writeAndFlush(response400).addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		removeHttp(ctx);
		super.channelInactive(ctx);
	}
	
	private void removeHttp(ChannelHandlerContext ctx){
		Attribute<String> attr = ctx.attr(HTTP_REQUESTID_KEY);
        if(attr.get() != null){
        	String requestid = attr.get();
        	HttpChannelManager.remove(requestid);
        }
	}
}  