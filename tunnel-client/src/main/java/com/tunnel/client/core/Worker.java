package com.tunnel.client.core;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.HttpData;
import com.tunnel.common.HttpUtil;
import com.tunnel.common.TunnelUtil;

public class Worker {
    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);
    
	/**
	 * 取件
	 */
	public byte[] pickup(String packId){
		SocketChannel socketChannel = null;  
        try {  
            socketChannel = SocketChannel.open();  
            SocketAddress socketAddress = new InetSocketAddress(Config.SERVER_IP, Config.PICKUP_PORT);  
            socketChannel.connect(socketAddress);  
            
            TunnelUtil.sendData(socketChannel, packId.getBytes());  
            TunnelUtil.sendEnd(socketChannel);
            
            List<byte[]> receiveData = TunnelUtil.receiveData(socketChannel);
            if(receiveData.size() > 0){
            	return receiveData.get(0);
            }
        } catch (Exception ex) {  
        	LOGGER.error("取件失败", ex);  
        } finally {
        	try {
        		if(socketChannel != null){
        			socketChannel.close();
        		}
			} catch (Exception e2) {}
        }
        return null;
	}
	
	/**
	 * 派送
	 * @throws Exception 
	 */
	public HttpData send(String host,byte[] data) throws Exception{
		IpAndPort ipAndPort = Config.HOST_MAP.get(host);
		if(ipAndPort == null){
			return null;
		}
		SocketChannel socketChannel = null;  
        try {  
            socketChannel = SocketChannel.open();  
            SocketAddress socketAddress = new InetSocketAddress(ipAndPort.getIp(), ipAndPort.getPort());  
            socketChannel.connect(socketAddress);  

            TunnelUtil.sendData(socketChannel, data);
            HttpData httpData = HttpUtil.readData(socketChannel);
            if(httpData == null || !httpData.isOk()){
            	throw new Exception("io error");
            }else{
            	return httpData;
            }
        } catch (Exception ex) {  
        	LOGGER.error("包裹处理结果提交失败", ex);
        	throw new Exception(ex.getMessage());
        } finally {
        	try {
        		if(socketChannel != null){
        			socketChannel.close();
        		}
			} catch (Exception e2) {}
        }
	}
	
	/**
	 * 答复
	 * 这里的data是从终端拿到的请求处理结果数据
	 */
	public void reply(String packId,HttpData data){
		SocketChannel socketChannel = null;  
        try {  
            socketChannel = SocketChannel.open();  
            SocketAddress socketAddress = new InetSocketAddress(Config.SERVER_IP, Config.REPLY_PORT);  
            socketChannel.connect(socketAddress);  

            //先发送包裹id
            TunnelUtil.sendData(socketChannel, packId.getBytes());
            TunnelUtil.sendEnd(socketChannel);
            //再发送数据内容
            TunnelUtil.sendData(socketChannel, data.getHeader());
            TunnelUtil.sendData(socketChannel, data.getData());
            TunnelUtil.sendEnd(socketChannel);
            //服务端拿到的是两个包裹，第一个包裹是id，第二个是数据
        } catch (Exception ex) {  
        	LOGGER.error("包裹处理结果提交失败", ex);  
        } finally {
        	try {
        		if(socketChannel != null){
        			socketChannel.close();
        		}
			} catch (Exception e2) {}
        }
	}
	
	public void reply(String packId,byte[] data){
		SocketChannel socketChannel = null;  
        try {  
            socketChannel = SocketChannel.open();  
            SocketAddress socketAddress = new InetSocketAddress(Config.SERVER_IP, Config.REPLY_PORT);  
            socketChannel.connect(socketAddress);  

            //先发送包裹id
            TunnelUtil.sendData(socketChannel, packId.getBytes());
            TunnelUtil.sendEnd(socketChannel);
            //再发送数据内容
            TunnelUtil.sendData(socketChannel, data);
            TunnelUtil.sendEnd(socketChannel);
            //服务端拿到的是两个包裹，第一个包裹是id，第二个是数据
        } catch (Exception ex) {  
        	LOGGER.error("包裹处理结果提交失败", ex);  
        } finally {
        	try {
        		if(socketChannel != null){
        			socketChannel.close();
        		}
			} catch (Exception e2) {}
        }
	}
	
}
