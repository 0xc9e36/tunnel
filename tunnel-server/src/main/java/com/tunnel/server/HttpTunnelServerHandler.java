package com.tunnel.server;

import java.io.InputStream;
import java.net.Socket;

public class HttpTunnelServerHandler extends Thread {

	protected Socket client;
	
	public HttpTunnelServerHandler(Socket _client){
		this.client = _client;
		start();
	}
	public void run(){
		try {
			InputStream clientIn = client.getInputStream();
			byte[] dataBuf = new byte[1024];
			int len = 0;
			StringBuilder tunnel = new StringBuilder();
			while((len = clientIn.read(dataBuf, 0, dataBuf.length)) != -1){
				for(int i=0;i<len;i++){
					tunnel.append((char)dataBuf[i]);
				}
			}
			
			//System.out.println(requestLine+" "+i+" "+j);
			//记录终端地址
			Tunnel.addTunnel(tunnel.toString(), client.getRemoteSocketAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				client.close();
			}catch(Exception ex){}
		}
	}
}
