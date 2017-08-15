package com.tunnel.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端通讯录
 */
public class TunnelManager {

	private static Map<String,Tunnel> TUNNEL_MAP = new HashMap<>();
	
	public synchronized static boolean add(Tunnel tunnel){
		if(!TUNNEL_MAP.containsKey(tunnel.getHost())){
			System.out.println("上线："+tunnel.getHost()+"-"+tunnel.getClientName());
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
			System.out.println("掉线："+tunnel.getHost()+"-"+tunnel.getClientName());
		}
	}
}
