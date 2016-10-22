package com.tunnel.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpRequestServerHandler extends Thread {
	public static Boolean lock = true;

	protected Socket client;
	public String method = "";
	public String tunnel = "";

	public HttpRequestServerHandler(Socket _client) {
		this.client = _client;
		start();
	}
	
	public void run(){
		InputStream clientIn = null;
		OutputStream clientOut = null;
		try {
			clientIn = client.getInputStream();
			byte[] data = request(clientIn);
			if(data == null || data.length == 0)
				return;
			
			synchronized (lock) {
				//转发请求给终端Tunnel-client
				Socket tunnelSocket = Tunnel.getTunnel(tunnel);
				if(tunnelSocket == null)
					return;
				
				OutputStream tunnelOut = tunnelSocket.getOutputStream();
				tunnelOut.write(data, 0, data.length);
				HttpUtil.endSend(tunnelOut);
				tunnelOut.flush();
				
				InputStream tunnelIn = tunnelSocket.getInputStream();
				byte[] dataBuf = new byte[1024];
				int len = 0;
				clientOut = client.getOutputStream();
	        	String endFlag = HttpUtil.getFlag();
				while((len = tunnelIn.read(dataBuf,0,dataBuf.length)) != -1){
					//判断结尾
					byte[] tunnelData = new byte[len];
					
					System.arraycopy(dataBuf, 0, tunnelData, 0, len);
					String flagPic = HttpUtil.filterEnd(tunnelData);
					if(endFlag.endsWith(flagPic)){
						if(len-flagPic.length() > 0){
							clientOut.write(dataBuf, 0, len-flagPic.length());
						}
						break;
					}else{
						clientOut.write(dataBuf, 0, len);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if(clientIn != null){
					clientIn.close();
				}
				if(clientOut != null){
					clientOut.close();
				}
				client.close();
			}catch(Exception ex){}
		}
	}
	
	
	protected byte[] request(InputStream clientIn){
		try {
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
						this.method = words[0];
						String path = words[1];
						path = path.startsWith("/")?path.substring(1):path;
						words = path.split("/");
						if(words.length > 0){
							this.tunnel = words[0];
						}
					}
				}
				
				if(this.method.equalsIgnoreCase("POST") || this.method.equals("PUT")){
					try {
						//TODO:就这么read，不够吧。
						if(clientIn.available() > 0){
							byte[] payload =new byte[1024];
							int n = clientIn.read(payload);
							byte[] dataWidthPayload = new byte[data.length+n];
							System.arraycopy(data, 0, dataWidthPayload, 0, data.length);
							System.arraycopy(payload, 0, dataWidthPayload, data.length, n);
							data = dataWidthPayload;
						}
					} catch (Exception e) {
						e.printStackTrace();
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
