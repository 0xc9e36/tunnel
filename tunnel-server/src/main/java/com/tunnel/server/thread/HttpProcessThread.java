package com.tunnel.server.thread;

import java.nio.channels.SocketChannel;

import com.tunnel.common.HttpData;
import com.tunnel.common.HttpUtil;
import com.tunnel.common.StringUtil;
import com.tunnel.common.TunnelUtil;
import com.tunnel.server.core.PackManager;
import com.tunnel.server.core.TunnelManager;

/**
 * http请求处理线程
 */
public class HttpProcessThread extends Thread{

	private SocketChannel socketChannel = null;
	
	
	public HttpProcessThread(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void run() {
		String packId = null;
		try {
			do{
				HttpData data = HttpUtil.readData(socketChannel);
				if(data == null || !data.isOk()){
					TunnelUtil.sendData(socketChannel, "400 bad request".getBytes());
					break;
				}
				
				//把数据放在包裹中心
				packId = PackManager.add(data);
				String host = HttpUtil.getHost(data.getHeader());
				//通知客户端来取包裹并处理掉包裹
				boolean askResult = TunnelManager.ask(packId,host);
				if(!askResult){
					TunnelUtil.sendData(socketChannel, ("404 resource not found ("+host+" client not fount)").getBytes());
					break;
				}
				//等待客户端处理数据包，总共10秒钟
				byte[] reply = null;
				for(int i=0;i<100;i++){
					try {
						reply = PackManager.getReply(packId);
						if(reply != null) break;
						Thread.sleep(100);
					} catch (Exception e) {}
				}
				if(reply == null){
					TunnelUtil.sendData(socketChannel, "404 source not fount (may be time out in 10 sec)".getBytes());
					break;
				}
				
				TunnelUtil.sendData(socketChannel, reply);
			}while(false);
		} catch (Exception e) {
			try {
				TunnelUtil.sendData(socketChannel, ("500 server error "+e.getMessage()).getBytes());
			} catch (Exception e2) {}
		} finally {
			try {
				socketChannel.close();
			} catch (Exception e2) {}
			try {
				//清空数据包
				if(StringUtil.isNotEmpty(packId)){
					PackManager.remove(packId);
				}
			} catch (Exception e2){}
		}
	}
}
