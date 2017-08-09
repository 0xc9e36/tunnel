package com.tunnel.client.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.TunnelUtil;

public class Register {
    private static final Logger LOGGER = LoggerFactory.getLogger(Register.class);
    
	public SocketChannel excute(){
		SocketChannel socketChannel = null;
        try {
        	String localHost = "...";
    		try {
    			localHost = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {}
    		String content = Config.NAME+"["+localHost+"]"+"#_NAME-#"+Config.HOST_ARY;
    		socketChannel = SocketChannel.open();  
            SocketAddress socketAddress = new InetSocketAddress(Config.SERVER_IP, Config.REGISTER_PORT);  
            socketChannel.connect(socketAddress);  
            
            TunnelUtil.sendData(socketChannel, content.getBytes());  
            TunnelUtil.sendEnd(socketChannel);
        } catch (Exception ex) {  
        	LOGGER.error("发起注册失败", ex);  
        }
        return socketChannel;
	}
}
