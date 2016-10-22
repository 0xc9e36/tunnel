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
			int len = clientIn.read(dataBuf, 0, dataBuf.length);
			StringBuilder tunnel = new StringBuilder();
			for(int i=0;i<len;i++){
				tunnel.append((char)dataBuf[i]);
			}
			
			//System.out.println(requestLine+" "+i+" "+j);
			//记录终端地址
			Tunnel.addTunnel(tunnel.toString(), client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
