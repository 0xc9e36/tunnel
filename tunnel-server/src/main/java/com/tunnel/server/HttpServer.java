package com.tunnel.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;


public class HttpServer extends Http {
	public HttpServer(Socket _client){
		super(_client);
	}
	public void run(){
		ArrayList<String> list = request();
		if(list.size()==0)
			return ;
		final String requestLine = list.get(0);
		if(requestLine.length()==0)
			return;
		try {
			//转发到Client
			String tunnel = getTunnel(requestLine);
			if(tunnel != null){
				TunnelServer.requestToClient(tunnel, client.getInputStream(), client.getOutputStream(), list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			client.close();
		}catch(Exception ex){}
	}
	
	public String getTunnel(String requestLine){
		String[] requestLineWords = requestLine.split(" ");
		if(requestLineWords.length > 1){
			String fullPath = requestLineWords[1];
			if(fullPath.startsWith("/")){
				fullPath = fullPath.substring(1);
				String[] fullPathWords = fullPath.split("/");
				if(fullPathWords.length > 0){
					String tunnel = fullPathWords[0];
					return tunnel;
				}
			}
		}
		return null;
	}
}
