package com.tunnel.server;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class TunnelServerHandler extends ChannelInboundHandlerAdapter { // (1)
	
	public static Map<String,Tunnel> TUNNEL_CHANNEL_MAP = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
    	ByteBuf in = (ByteBuf) msg;
        try {
        	StringBuilder tunnel = new StringBuilder();
            while (in.isReadable()) { // (1)
            	tunnel.append((char) in.readByte());
            }
            Tunnel tunnelEntity = new Tunnel();
            Channel channel = ctx.channel();
            tunnelEntity.setChannel(channel);
            TUNNEL_CHANNEL_MAP.put(tunnel.toString(), tunnelEntity);
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    
    public static void disaptchRequest(String tunnel,byte[] request,OutputStream out){
    	Tunnel tunnelEntity = TUNNEL_CHANNEL_MAP.get(tunnel);
    	try {
    		if(tunnelEntity != null){
    			Channel channel = tunnelEntity.getChannel();
    			tunnelEntity.setOut(out);
    			final ByteBuf time = channel.alloc().buffer(request.length); // (2)

    			time.writeBytes(request);
    			channel.writeAndFlush(time);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void disaptchResponse(String tunnel,byte[] response){
    	Tunnel tunnelEntity = TUNNEL_CHANNEL_MAP.get(tunnel);
    	try {
    		if(tunnelEntity != null && tunnelEntity.getOut() != null){
    			OutputStream out = tunnelEntity.getOut();
    			out.write(response);
    			out.flush();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
