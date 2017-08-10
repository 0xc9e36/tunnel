package com.tunnel.server.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tunnel.common.TunnelUtil;

/**
 * 客户端通讯录
 */
public class TunnelManager {

	private static Map<String,Tunnel> TUNNEL_MAP = new HashMap<>();
	
	public synchronized static boolean register(Tunnel tunnel){
		if(!TUNNEL_MAP.containsKey(tunnel.getHost())){
			TUNNEL_MAP.put(tunnel.getHost(), tunnel);
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized static boolean ask(String packId,String host) throws IOException{
		Tunnel tunnel = TUNNEL_MAP.get(host);
		if(tunnel != null){
			String command = "#_ASK-#"+packId+"#_SPLIT-#"+host;
			TunnelUtil.sendData(tunnel.getNotifyChannel(), command.getBytes());
			TunnelUtil.sendEnd(tunnel.getNotifyChannel());
			return true;
		}
		return false;
	}
	
	public synchronized  static void removeOneDied(){
		Tunnel diedClient = null;
		for(Map.Entry<String, Tunnel> entry:TUNNEL_MAP.entrySet()){
			if(entry.getValue().getNotifyChannel().isConnected() == false){
				diedClient = entry.getValue();
				break;
			}
			
			try {
				TunnelUtil.sendData(entry.getValue().getNotifyChannel(), "#_HEART-#".getBytes());
				TunnelUtil.sendEnd(entry.getValue().getNotifyChannel());
			} catch (Exception e) {
				try {
					entry.getValue().getNotifyChannel().close();
				} catch (Exception e2) {}
				diedClient = entry.getValue();
				break;
			}
			
		}
		if(diedClient != null){
			TUNNEL_MAP.remove(diedClient.getHost());
			System.out.println("掉线："+diedClient.getHost()+" - "+diedClient.getClientName());
		}
	}
}
