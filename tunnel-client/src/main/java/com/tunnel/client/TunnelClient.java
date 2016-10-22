package com.tunnel.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TunnelClient {
	private static String tunnelHostAddr = "101.200.87.104";
	private static int tunnelHostPort = 8020;
	
	private final static int TIME_OUT = 10000;
	public static void main(String[] args) throws Exception {
        //汇报tunnel
		final Socket login = new Socket(tunnelHostAddr, tunnelHostPort);
		OutputStream loginOut = null;
		try {
			login.setSoTimeout(TIME_OUT);
			loginOut = login.getOutputStream();
			//TODO:tunnel
			byte[] data = "gaspipe".getBytes();
			loginOut.write(data,0,data.length);
			loginOut.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(loginOut != null){
				try {
					loginOut.close();
				} catch (Exception e2) {
				}
			}
			login.close();
		}
		
		Thread tunnelClient =new Thread(new Runnable(){
			public void run(){ 
				try{
					ServerSocket httpServer = getServer(login.getLocalPort());
					String info = "HTTP-TUNNEL SERVER START: ";
					info += "IP= "+InetAddress.getLocalHost();
					info += "\tPORT= "+httpServer.getLocalPort();
					System.out.println(info);
					while(true)
						new TunnelClientHandler(httpServer.accept());
				}catch(Exception ex){ }
			}
		});
		tunnelClient.start();
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
