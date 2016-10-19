package com.tunnel.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TunnelClient extends Thread{
	private static Logger LOGGER = LoggerFactory.getLogger(TunnelClient.class);
	
	private Socket client = null;
	

	public TunnelClient(Socket client) {
		this.client = client;
		start();
	}

	
	@Override
	public void run() {
		try {
			OutputStream out = client.getOutputStream();
			PrintWriter pw = new PrintWriter(out,true);
			//TODO:
			pw.println("cdy");
			pw.println("cdy");
			pw.println("gaspipe");
			
			InputStream in = client.getInputStream();
			int t;
			StringBuilder line = new StringBuilder();
			do{
				line.setLength(0);
				while(((t=in.read())!='\r' || (t=in.read())!='\n') && t>0){ //13\r 10\n
					//TODO 客户端可能不发送数据，或不规范的数据
					line.append((char)t);
				}
				LOGGER.debug(line.toString());
			}while(line.length()>0); 
		} catch (Exception e) {
			
		}
		LOGGER.debug("Tunnel Client STOPPED");
	}
}
