package com.tunnel.server;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tunnel {
	private static Logger LOGGER = LoggerFactory.getLogger(Tunnel.class);

	private static Map<String,SocketAddress> TUNNEL_MAP = new HashMap<>();
	
	
	
	public static synchronized void addTunnel(String tunnel,SocketAddress address){
		TUNNEL_MAP.put(tunnel, address);
		LOGGER.info(tunnel+" connected");
	}
	
	public static synchronized SocketAddress getTunnel(String tunnel){
		return TUNNEL_MAP.get(tunnel);
	}
}
