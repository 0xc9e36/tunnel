package com.tunnel.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TunnelClient {
	private static String tunnelHostAddr = "101.200.87.104";
//	private static String tunnelHostAddr = "localhost";
	private static int tunnelHostPort = 8020;
	
	public static void main(String[] args) throws Exception {
		if(args == null || args.length < 3){
			System.out.println("参数不对：[tunnel host port]");
		}else{
			final String tunnelName = args[0];
			final String host = args[1];
			try {
				final int port = Integer.parseInt(args[2]);
				
				
				
				//汇报tunnel
				final Socket tunnel = new Socket(tunnelHostAddr, tunnelHostPort);
				OutputStream loginOut = null;
				try {
					loginOut = tunnel.getOutputStream();
					//TODO:tunnel
					byte[] data = tunnelName.getBytes();
					loginOut.write(data,0,data.length);
					loginOut.flush();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Thread tunnelClient =new Thread(new Runnable(){
					public void run(){ 
						try{
							String info = "HTTP-TUNNEL SERVER START: ";
							info += "IP= "+InetAddress.getLocalHost();
							info += "\tPORT= "+tunnel.getLocalPort();
							System.out.println(info);
//							while(true)
								new TunnelClientHandler(tunnel,tunnelName,host,port);
						}catch(Exception ex){ }
					}
				});
				tunnelClient.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
    }
	
	
	public static ServerSocket getServer(int port){
		ServerSocket server = null;
		int c = 0;
		while(server==null && ++c<100){
			try {
				server = new ServerSocket(port);
				return server;
			} catch (IOException e) {
				port += 3*c + 1;
				//e.printStackTrace();
			}
		}
		return null;
	}
}
