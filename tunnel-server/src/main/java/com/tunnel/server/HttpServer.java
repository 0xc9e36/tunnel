package com.tunnel.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class HttpServer {
	
	public static void start() {
		Thread httpReq =new Thread(new Runnable(){
			public void run(){
				try{
					ServerSocket httpServer = getServer(8010);
					String info = " HTTP-REQUEST SERVER START: ";
					info += "IP= "+InetAddress.getLocalHost();
					info += "\tPORT= "+httpServer.getLocalPort();
					System.out.println(info);
					while(true)
						new HttpRequestServerHandler(httpServer.accept());
				}catch(Exception ex){ }
			}
		});
		httpReq.start();
		
		Thread httpRes =new Thread(new Runnable(){
			public void run(){ 
				try{
					ServerSocket httpServer = getServer(8020);
					String info = "HTTP-TUNNEL SERVER START: ";
					info += "IP= "+InetAddress.getLocalHost();
					info += "\tPORT= "+httpServer.getLocalPort();
					System.out.println(info);
					while(true)
						new HttpTunnelServerHandler(httpServer.accept());
				}catch(Exception ex){ }
			}
		});
		httpRes.start();
	}
	

	public static ServerSocket getServer(int port){
		ServerSocket server = null;
		int c = 0;
		while(server==null && ++c<100){
			try {
				server = new ServerSocket(port);
				return server;
			} catch (IOException e) {
				port += 3*c + 1;
				//e.printStackTrace();
			}
		}
		return null;
	}
}
