package com.tunnel.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TunnelServer {
	private static Logger LOGGER = LoggerFactory.getLogger(TunnelServer.class);
	
	private static Map<String,Socket> CLIENT_MAP = new HashMap<String,Socket>();

	private TunnelServer() {}
	
	
	public static void addClient(Socket client){
		try {
			InputStream in = client.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String userName = reader.readLine();
			String password = reader.readLine();
			String tunnelStr = reader.readLine();
			String[] tunnels = null;
			if(tunnelStr != null && !"".equals(tunnelStr)){
				tunnels = tunnelStr.split(",");
			}
			
			if(tunnels != null){
				LOGGER.info(userName+" connected");
				synchronized (CLIENT_MAP) {
					for(String tunnel:tunnels){
						//tunnel唯一
						CLIENT_MAP.put(tunnel, client);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void requestToClient(String tunnel,InputStream req,OutputStream res,List<String> request){
		Socket client = CLIENT_MAP.get(tunnel);
		if(client != null){
			synchronized (CLIENT_MAP) {
				client = CLIENT_MAP.get(tunnel);
				if(client != null){
					try {
						SocketAddress remoteSocketAddress = client.getRemoteSocketAddress();
						client.connect(remoteSocketAddress);
						//把请求转发给tunnel-client
						OutputStream tunnel_req = client.getOutputStream();
						PrintWriter printWriter = new PrintWriter(tunnel_req,true);
						if(request != null){
							for(String line:request){
								printWriter.write(line);
							}
						}
						
						byte[] data = new byte[1024];
						int len = 0;
						while((len=req.read(data, 0, data.length)) > 0){
							tunnel_req.write(data,0,len);
						}
						
						//把tunnel-client的响应返回给浏览器
						InputStream tunnel_res = client.getInputStream();
						while((len=tunnel_res.read(data, 0, data.length)) > 0){
							res.write(data,0,len);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
			}
		}
	}
}
