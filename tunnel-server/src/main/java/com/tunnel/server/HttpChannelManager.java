package com.tunnel.server;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;

public class HttpChannelManager {
private static Map<String,ChannelHandlerContext> CHANNEL_MAP = new HashMap<>();
	
	public synchronized static void add(String requestId,ChannelHandlerContext cx){
		CHANNEL_MAP.put(requestId, cx);
	}
	
	public synchronized static ChannelHandlerContext get(String requestid){
		return remove(requestid);
	}

	public static ChannelHandlerContext remove(String requestid) {
		return CHANNEL_MAP.remove(requestid);
	}
}
