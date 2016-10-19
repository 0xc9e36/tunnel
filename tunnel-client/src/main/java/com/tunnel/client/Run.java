package com.tunnel.client;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Run {
	private static Logger LOGGER = LoggerFactory.getLogger(Run.class);
	
	public static void main(String[] args) {
		Thread httpt =new Thread(new Runnable(){
			public void run(){
				try{
					Socket client = connectServer();
					if(client != null){
						LOGGER.info("Tunnel Client START");
						new TunnelClient(client);
					}else{
						LOGGER.info("Connect to Tunnel-Server FAILED");
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		httpt.start();
	}

	public static Socket connectServer(){
		try {
			Socket client = new Socket("127.0.0.1", 8020);
			return client;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
