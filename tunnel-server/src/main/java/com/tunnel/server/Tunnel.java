package com.tunnel.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tunnel {
	private static Logger LOGGER = LoggerFactory.getLogger(Tunnel.class);

	private static Map<String,Socket> TUNNEL_MAP = new HashMap<>();
	
	
	
	public static synchronized void addTunnel(String tunnel,Socket socket){
		TUNNEL_MAP.put(tunnel, socket);
		LOGGER.info(tunnel+" connected");
	}
	
	public static synchronized Socket getTunnel(String tunnel){
		return TUNNEL_MAP.get(tunnel);
	}
}
