package com.tunnel.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TunnelClientHandler extends Thread {
	protected Socket client;
	private String tunnel = "gaspipe";
	private String host = "localhost";
	private int port = 8082;
	private final static int TIME_OUT = 10000;

	public TunnelClientHandler(Socket _client) {
		this.client = _client;
		start();
	}
	
    public void run() {
    	InputStream clientIn = null;
    	OutputStream clientOut = null;
    	try {
    		clientIn = client.getInputStream();
    		clientOut = client.getOutputStream();
    		StringBuilder request = new StringBuilder();
        	String endFlag = HttpUtil.getFlag();
    		try {
    			byte[] dataBuf = new byte[1024];
    			int len = 0;
    			while((len = clientIn.read(dataBuf,0,dataBuf.length)) != -1){
    				for(int i=0;i<len;i++){
    					request.append((char)dataBuf[i]);
    				}
    				
    				//判断结尾
    				byte[] data = new byte[len];
    				System.arraycopy(dataBuf, 0, data, 0, len);
    				String flagPic = HttpUtil.filterEnd(data);
    				if(endFlag.endsWith(flagPic)){
    					break;
    				}
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		String content = request.substring(0, request.length()-endFlag.length());
            
    		
    		//解析并转发给目标服务
            content = content.replaceFirst(tunnel+"/", "");
            if(content.indexOf("Host: localhost:8010") > 0){
            	content = content.replaceFirst("Host: localhost:8010", "Host: "+host+":"+port);
            }
            content = content.replaceFirst("Connection: keep-alive", "Connection: Close");
            
            response(content.getBytes(),clientOut);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(clientIn != null){
					clientIn.close();
				}
				if(clientOut != null){
					clientOut.close();
				}
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
