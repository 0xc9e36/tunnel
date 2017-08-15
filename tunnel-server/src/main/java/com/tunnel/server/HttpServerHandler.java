package com.tunnel.server;
  
import com.tunnel.common.Constant;
import com.tunnel.common.HttpData;
import com.tunnel.common.HttpUtil;
import com.tunnel.common.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;  
  
public class HttpServerHandler extends SimpleChannelInboundHandler<ByteBuf>{  
    public static final AttributeKey<String> HTTP_REQUESTID_KEY = AttributeKey.valueOf("http.requestid"); 
    
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
    	cause.printStackTrace();  
        Attribute<String> attr = ctx.attr(HTTP_REQUESTID_KEY);
        if(attr.get() != null){
        	String requestid = attr.get();
        	HttpChannelManager.remove(requestid);
        }
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		
		byte[] data = new byte[buf.readableBytes()];
		buf.getBytes(buf.readerIndex(), data);
	
		//数据中可能包含多个数据包
		//每个数据包都以结束符作为结束
		HttpData httpData = HttpUtil.analyzeHttpData(data);
		if(httpData != null && httpData.getHeader() != null){
			String host = HttpUtil.getHost(httpData.getHeader());
			host = host==null?"":host.trim();
			
			Tunnel tunnel = TunnelManager.get(host);
			if(tunnel != null){
				ChannelHandlerContext clientCtx = tunnel.getChannelHandlerContext();
				Attribute<String> attr = ctx.attr(HTTP_REQUESTID_KEY);
				String requestid = System.currentTimeMillis()+StringUtil.getRandomString(3);
//				Attribute<String> attr = ctx.attr(HTTP_REQUESTID_KEY);
				if(attr.get() == null){
					attr.setIfAbsent(requestid);
				}
				HttpChannelManager.add(requestid, ctx);
				super.channelActive(ctx);
				if(attr.get() != null){
					byte[] hostIndexBytes = String.valueOf(tunnel.getHostIndex()).getBytes();
					byte[] requestIdBytes = attr.get().getBytes();
					byte[] endFlagBytes = Constant.ENT_FLAG.getBytes();
					ByteBuf dispatchBuf = null;  
		        	dispatchBuf = Unpooled.buffer(hostIndexBytes.length+requestIdBytes.length+data.length+endFlagBytes.length);  
		        	dispatchBuf.writeBytes(hostIndexBytes);
		        	dispatchBuf.writeBytes(requestIdBytes);  
		        	dispatchBuf.writeBytes(data);
		        	dispatchBuf.writeBytes(endFlagBytes);
		        	clientCtx.writeAndFlush(dispatchBuf);  
				}
				
			}else{
				FullHttpResponse response404 = HttpUtil.response404();
				ctx.writeAndFlush(response404).addListener(ChannelFutureListener.CLOSE);
			}
		}else{
			FullHttpResponse response400 = HttpUtil.response400();
			ctx.writeAndFlush(response400).addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Attribute<String> attr = ctx.attr(HTTP_REQUESTID_KEY);
        if(attr.get() != null){
        	String requestid = attr.get();
        	HttpChannelManager.remove(requestid);
        }
		super.channelInactive(ctx);
	}
}  