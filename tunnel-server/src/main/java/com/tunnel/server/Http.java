package com.tunnel.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Http extends Thread {
	private static Logger LOGGER = LoggerFactory.getLogger(Http.class);

	protected final static String POST_TAG = "POST";
	protected final static String PUT_TAG = "PUT";
	protected Socket client;
	public Http(Socket _client) {
		this.client = _client;
		start();
	}
	protected ArrayList<String> request(){
		InputStream clientIn;
		ArrayList<String> list = new ArrayList<String>();
		try {
			clientIn = client.getInputStream();
			int t;
			StringBuilder line = new StringBuilder();
			do{
				line.setLength(0);
				while(((t=clientIn.read())!='\r' || (t=clientIn.read())!='\n') && t>0){ //13\r 10\n
					//TODO 客户端可能不发送数据，或不规范的数据
					line.append((char)t);
				}
//				LOGGER.debug(line.toString());
				list.add(line.toString());	//main function
			}while(line.length()>0); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
