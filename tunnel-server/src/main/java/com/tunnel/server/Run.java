package com.tunnel.server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Run {
	private static Logger LOGGER = LoggerFactory.getLogger(Run.class);
	
	public static void main(String[] args) {
		final int httpPort;
		Scanner scan =new Scanner(System.in);
		LOGGER.info("input http port:");
		httpPort = scan.nextInt();
		scan.close();
		//http
		Thread http =new Thread(new Runnable(){
			public void run(){
				try{
					ServerSocket httpServer = getServer(httpPort);
					String info = " HTTP SERVER START: ";
					info += "IP= "+InetAddress.getLocalHost();
					info += "\tPORT= "+httpServer.getLocalPort();
					LOGGER.info(info);
					while(true)
						new HttpServer(httpServer.accept());
				}catch(Exception ex){ }
			}
		});
		http.start();
		
		//tunnel
		Thread tunnel =new Thread(new Runnable(){
			public void run(){
				try{
					ServerSocket tunnelServer = getServer(8020);
					String info = " TUNNEL SERVER START: ";
					info += "IP= "+InetAddress.getLocalHost();
					info += "\tPORT= "+tunnelServer.getLocalPort();
					LOGGER.info(info);
					while(true)
						TunnelServer.addClient(tunnelServer.accept());
				}catch(Exception ex){ }
			}
		});
		tunnel.start();
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
			}
		}
		return null;
	}
}
