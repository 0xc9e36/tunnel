package com.tunnel.server;

import java.net.Socket;

public class HttpResponseServerHandler extends HttpResponse {

	public HttpResponseServerHandler(Socket _client){
		super(_client);
	}
	public void run(){
		byte[] data = request();
		if(data == null || data.length == 0)
			return ;
		try {
			//System.out.println(requestLine+" "+i+" "+j);
			//转发请求给终端Tunnel-client
			TunnelServerHandler.disaptchResponse(tunnel, data);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try{
			client.close();
		}catch(Exception ex){}
	}
}
