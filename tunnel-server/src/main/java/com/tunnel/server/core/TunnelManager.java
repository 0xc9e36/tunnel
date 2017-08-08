package com.tunnel.server.core;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tunnel.common.TunnelUtil;
import com.tunnel.common.StringUtil;

/**
 * 客户端通讯录
 */
public class TunnelManager {

	private static Map<Tunnel,SocketChannel> TUNNEL_MAP = new HashMap<>();
	
	public synchronized static void register(SocketChannel socketChannel) throws IOException{
		List<byte[]> receiveData = TunnelUtil.receiveData(socketChannel);
		for(byte[] data:receiveData){
			String content = new String(data);
			if(StringUtil.isNotEmpty(content) || content.contains("#_NAME-#")){
				String[] split = content.split("#_NAME-#");
				String clientName = split[0];
				String hostAry = split[1];
				String[] split2 = hostAry.split(",");
				
				List<String> successHostList = new ArrayList<>();
				List<String> failedHostList = new ArrayList<>();
				for(String host:split2){
					host = host == null?"":host.trim();
					if(StringUtil.isNotEmpty(host)){
						Tunnel client = new Tunnel(host, clientName);
						if(!TUNNEL_MAP.containsKey(client)){
							TUNNEL_MAP.put(client, socketChannel);
							successHostList.add(host);
						}else{
							failedHostList.add(host);
						}
					}
				}
				String success = "";
				for(String host:successHostList){
					if(StringUtil.isNotEmpty(success)){
						success = success+",";
					}
					success = success+host;
				}
				String failed = "";
				for(String host:failedHostList){
					if(StringUtil.isNotEmpty(failed)){
						failed = failed+",";
					}
					failed = failed+host;
				}
				
				TunnelUtil.sendData(socketChannel, ("#_REGISTER-#"+success+"#_SPLIT-#"+failed).getBytes());
				TunnelUtil.sendEnd(socketChannel);
			}
		}
	}
	
	public synchronized static boolean ask(String packId,String host) throws IOException{
		SocketChannel socketChannel = TUNNEL_MAP.get(new Tunnel(host, null));
		if(socketChannel != null){
			String command = "#_ASK-#"+packId+"#_SPLIT-#"+host;
			TunnelUtil.sendData(socketChannel, command.getBytes());
			TunnelUtil.sendEnd(socketChannel);
			return true;
		}
		return false;
	}
	
	public synchronized  static void removeOneDied(){
		Tunnel diedClient = null;
		for(Map.Entry<Tunnel, SocketChannel> entry:TUNNEL_MAP.entrySet()){
			if(entry.getValue().isConnected() == false){
				diedClient = entry.getKey();
				break;
			}
			
			try {
				TunnelUtil.sendData(entry.getValue(), "#_HEART-#".getBytes());
				TunnelUtil.sendEnd(entry.getValue());
			} catch (Exception e) {
				try {
					entry.getValue().close();
				} catch (Exception e2) {}
				diedClient = entry.getKey();
				break;
			}
			
		}
		if(diedClient != null){
			TUNNEL_MAP.remove(diedClient);
			System.out.println("移除："+diedClient.getHost());
		}
	}
}
