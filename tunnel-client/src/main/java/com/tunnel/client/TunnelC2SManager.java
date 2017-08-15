package com.tunnel.client;

import io.netty.channel.ChannelHandlerContext;

public class TunnelC2SManager {

	private static ChannelHandlerContext channelHandlerContext;

	private TunnelC2SManager() {}
	
	public synchronized static ChannelHandlerContext getChannelHandlerContext() {
		return channelHandlerContext;
	}

	public synchronized static void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
		TunnelC2SManager.channelHandlerContext = channelHandlerContext;
	}
}
