package com.tunnel.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端通讯录
 */
public class TunnelManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(TunnelManager.class);
	
	private static Map<String,Tunnel> TUNNEL_MAP = new HashMap<>();
	
	public synchronized static boolean add(Tunnel tunnel){
		if(!TUNNEL_MAP.containsKey(tunnel.getHost())){
			LOGGER.info("上线："+tunnel.getHost()+"-"+tunnel.getClientName());
			
			TUNNEL_MAP.put(tunnel.getHost(), tunnel);
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized static Tunnel get(String host){
		return TUNNEL_MAP.get(host);
	}
	
	public synchronized  static void remove(String host){
		Tunnel tunnel = TUNNEL_MAP.remove(host);
		if(tunnel != null){
			LOGGER.info("掉线："+tunnel.getHost()+"-"+tunnel.getClientName());
		}
	}
	
	public synchronized  static Collection<Tunnel> getTunnels(){
		return TUNNEL_MAP.values();
	}
}
