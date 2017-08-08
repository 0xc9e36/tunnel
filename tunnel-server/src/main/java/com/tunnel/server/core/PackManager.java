package com.tunnel.server.core;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tunnel.common.CollectionUtil;
import com.tunnel.common.HttpData;
import com.tunnel.common.StringUtil;
import com.tunnel.common.TunnelUtil;

/**
 * 包裹管理
 */
public class PackManager {

	private static Map<String,Pack> PACK_MAP = new HashMap<>();
	
	public static synchronized String add(HttpData request){
		String packId = System.currentTimeMillis()+StringUtil.getRandomString(3);
		
		PACK_MAP.put(packId, new Pack(request));
		
		return packId;
	}
	
	public synchronized static void pickup(SocketChannel socketChannel) throws IOException{
		List<byte[]> receiveData = TunnelUtil.receiveData(socketChannel);
		Pack pack = null;
		for(byte[] data:receiveData){
			String packId = new String(data);
			if(StringUtil.isNotEmpty(packId)){
				pack = PACK_MAP.get(packId);
				break;
			}
		}
		if(pack != null){
			//头信息
			TunnelUtil.sendData(socketChannel, pack.getRequest().getHeader());
			//请求体
			TunnelUtil.sendData(socketChannel, pack.getRequest().getData());
			
		}
		TunnelUtil.sendEnd(socketChannel);
		
		try {
			socketChannel.close();
		} catch (Exception e) {}
	}
	
	public static synchronized void setReply(SocketChannel socketChannel) throws IOException{
		List<byte[]> receiveData = TunnelUtil.receiveData(socketChannel);
		if(CollectionUtil.isNotEmpty(receiveData) && receiveData.size() == 2){
			//第一个包裹是packId
			//第二个包裹是处理结果数据包
			byte[] data1 = receiveData.get(0);
			String packId = new String(data1);
			if(StringUtil.isNotEmpty(packId)){
				Pack pack = PACK_MAP.get(packId);
				if(pack != null){
					//如果pack还在的话，就可以放置返回结果了
					pack.setResponse(receiveData.get(1));
				}
			}
		}

		try {
			socketChannel.close();
		} catch (Exception e) {}
	}
	
	public static synchronized byte[] getReply(String packId){
		Pack pack = PACK_MAP.get(packId);
		if(pack != null){
			return pack.getResponse();
		}
		return null;
	}
	
	public static synchronized void remove(String packId){
		PACK_MAP.remove(packId);
	}
	
	
}
