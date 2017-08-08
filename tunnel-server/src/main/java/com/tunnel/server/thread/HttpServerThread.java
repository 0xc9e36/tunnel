package com.tunnel.server.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.server.core.Config;

/**
 * Http服务线程
 * 负责如下事务：
 * 接受http请求，
 * 多线程下发通知
 */
public class HttpServerThread extends Thread{
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerThread.class);

	@Override
	public void run() {
		Selector selector = null;  
        ServerSocketChannel serverSocketChannel = null;  
          
        try {  
            // Selector for incoming time requests  
            selector = Selector.open();  
  
            // Create a new server socket and set to non blocking mode  
            serverSocketChannel = ServerSocketChannel.open();  
            serverSocketChannel.configureBlocking(false);  
              
            // Bind the server socket to the local host and port  
            serverSocketChannel.socket().setReuseAddress(true);  
            serverSocketChannel.socket().bind(new InetSocketAddress(Config.HTTP_SERVER_PORT));  
              
            // Register accepts on the server socket with the selector. This  
            // step tells the selector that the socket wants to be put on the  
            // ready list when accept operations occur, so allowing multiplexed  
            // non-blocking I/O to take place.  
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  
      
            // Here's where everything happens. The select method will  
            // return when any operations registered above have occurred, the  
            // thread has been interrupted, etc.  
            while (selector.select() > 0) {  
                // Someone is ready for I/O, get the ready keys  
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();  
      
                // Walk through the ready keys collection and process date requests.  
                while (it.hasNext()) {  
                    SelectionKey readyKey = it.next();  
                    it.remove();  
                      
                    // The key indexes into the selector so you  
                    // can retrieve the socket that's ready for I/O
                    ServerSocketChannel ssc = (ServerSocketChannel) readyKey.channel();
                    if(ssc != null){
                    	SocketChannel socketChannel = ssc.accept();  
                        new HttpProcessThread(socketChannel).start();
                    }
                }  
            }  
        } catch (ClosedChannelException ex) {  
        	LOGGER.error("关闭http服务失败",ex);
        } catch (IOException ex) {  
        	LOGGER.error("http服务发生异常", ex);  
        } finally {  
            try {  
            	if(selector != null){
            		selector.close();
            	}
            } catch(Exception ex) {}  
            try {
            	if(serverSocketChannel != null){
            		serverSocketChannel.close();  
            	}
            } catch(Exception ex) {}  
        }  
	}
}
