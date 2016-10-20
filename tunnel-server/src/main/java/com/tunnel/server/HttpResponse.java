package com.tunnel.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public abstract class HttpResponse extends Thread {

	protected Socket client;
	
	public String tunnel = "";
	
	public HttpResponse(Socket _client) {
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
				
				StringBuilder firstLine = new StringBuilder();
				for(byte b:dataBuf){
					char c = (char)b;
					if(c != '\r' && c != '\n'){
						firstLine.append(c);
					}else{
						break;
					}
				}
				
				if(firstLine.length() > 0){
					this.tunnel = firstLine.toString();
				}
				//把tunnel剔除掉
				byte[] data = new byte[len];
				System.arraycopy(dataBuf, firstLine.length(), data, firstLine.length(), len-firstLine.length());
				return data;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
