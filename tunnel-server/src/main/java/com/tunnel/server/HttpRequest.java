package com.tunnel.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public abstract class HttpRequest extends Thread {

	protected Socket client;
	
	public String tunnel = "";
	
	public HttpRequest(Socket _client) {
		this.client = _client;
		start();
	}
	protected byte[] request(){
		InputStream clientIn;
		try {
			clientIn = client.getInputStream();
			byte[] dataBuf = new byte[2048];
			int len = clientIn.read(dataBuf,0,dataBuf.length);
			if(len > 0){
				byte[] data = new byte[len];
				System.arraycopy(dataBuf, 0, data, 0, len);
				
				StringBuilder firstLine = new StringBuilder();
				for(byte b:data){
					char c = (char)b;
					if(c != '\r' && c != '\n'){
						firstLine.append(c);
					}else{
						break;
					}
				}
				
				if(firstLine.length() > 0){
					String[] words = firstLine.toString().split(" ");
					if(words.length > 1){
						String path = words[1];
						path = path.startsWith("/")?path.substring(1):path;
						words = path.split("/");
						if(words.length > 0){
							this.tunnel = words[0];
						}
					}
				}
				return data;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
