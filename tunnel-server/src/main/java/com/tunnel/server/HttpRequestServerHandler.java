package com.tunnel.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpRequestServerHandler extends HttpRequest {

	public HttpRequestServerHandler(Socket _client){
		super(_client);
	}
	public void run(){
		byte[] data = request();
		if(data == null || data.length == 0)
			return ;
		try {
			//System.out.println(requestLine+" "+i+" "+j);
			OutputStream output = client.getOutputStream();
			//转发请求给终端Tunnel-client
			TunnelServerHandler.disaptchRequest(tunnel, data, output);
		} catch (IOException e) {
			//e.printStackTrace();
		}
		try{
			client.close();
		}catch(Exception ex){}
	}
}
