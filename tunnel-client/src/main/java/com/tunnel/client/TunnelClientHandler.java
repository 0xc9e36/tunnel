package com.tunnel.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TunnelClientHandler extends Thread {
	protected Socket client;
	private String tunnel;
	private String host;
	private int port;
	private final static int TIME_OUT = 10000;

	public TunnelClientHandler(Socket _client,String tunnel,String host,int port) {
		this.client = _client;
		this.tunnel = tunnel;
		this.host = host;
		this.port = port;
		start();
	}
	
    public void run() {
    	try {
    		while(this.client.isConnected()){
    			byte[] data = new byte[0];
        		InputStream clientIn = client.getInputStream();
        		OutputStream clientOut = client.getOutputStream();
            	String endFlag = HttpUtil.getFlag();
        		try {
        			byte[] dataBuf = new byte[1024];
        			int len = 0;
        			while((len = clientIn.read(dataBuf,0,dataBuf.length)) != -1){
        				byte[] dataTmp = new byte[data.length+len];

        				System.arraycopy(data, 0, dataTmp, 0, data.length);
        				System.arraycopy(dataBuf, 0, dataTmp, data.length, len);
        				data = dataTmp;
        				
        				String flagPic = HttpUtil.filterEnd(dataTmp);
        				if(endFlag.endsWith(flagPic)){
        					data = new byte[dataTmp.length-endFlag.getBytes().length];
        					System.arraycopy(dataTmp, 0, data, 0, data.length);
        					break;
        				}else{
            				data = dataTmp;
        				}
        			}
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		

        		data = HttpUtil.replaceFirst(data, tunnel+"/", "");
        		//TODO:需要换成线上
        		data = HttpUtil.replaceFirst(data, "Host: localhost:8010", "Host: "+host+":"+port);
        		data = HttpUtil.replaceFirst(data,"Connection: keep-alive", "Connection: Close");
        		
        		/*String request = new String(data,"UTF-8");
        		String content = request.substring(0, request.length()-endFlag.length());
                
        		
        		//解析并转发给目标服务
                content = content.replaceFirst(tunnel+"/", "");
                if(content.indexOf("Host: localhost:8010") > 0){
                	content = content.replaceFirst("Host: localhost:8010", "Host: "+host+":"+port);
                }
                content = content.replaceFirst("Connection: keep-alive", "Connection: Close");
                System.out.println(content);*/
        		HttpUtil.logData(data);
        		if(data.length > 0){
        			response(data,clientOut);
        		}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void response(byte[] data,OutputStream clientOut){
    	Socket server = null;
    	InputStream serverIn = null;
    	OutputStream serverOut = null;
		try {
			//向目标网站发送请求
			server = new Socket(host,port);	
			server.setSoTimeout(TIME_OUT);
			serverOut = server.getOutputStream();
			serverOut.write(data,0,data.length);
			serverOut.flush();
			
			//将目标网站返回的数据中转给客户端
			serverIn = server.getInputStream();
			byte[] dataBuf = new byte[1024];
			int len = 0;
			while((len = serverIn.read(dataBuf,0,dataBuf.length)) != -1){
				clientOut.write(dataBuf, 0, len);
			}
			HttpUtil.endSend(clientOut);
			clientOut.flush();
			
			
		} catch (IOException e) {
			//System.out.println("Exception:"+list.get(0));
			e.printStackTrace();
		} finally {
			try {
				serverIn.close();
				serverOut.close();
				server.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
