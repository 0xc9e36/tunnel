/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tunnel.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;  
  
public class HttpServer extends Thread{  
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
    private int port;  
      
    public HttpServer(int port) {  
        this.port = port;  
        this.setName("HttpServer thread");
    }  
      
    @Override
    public void run(){  
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {  
            ServerBootstrap sbs = new ServerBootstrap()
            		.group(bossGroup,workerGroup)
                    .option(ChannelOption.TCP_NODELAY, true)
            		.channel(NioServerSocketChannel.class)  
                    .childHandler(new ChannelInitializer<SocketChannel>() {  
                        
                        protected void initChannel(SocketChannel ch) throws Exception {
                        	ch.pipeline().addLast(new TunnelHttpResponseEncoder());
//                        	ch.pipeline().addLast(new HttpResponseEncoder());
                        	ch.pipeline().addLast(new HttpObjectAggregator(1024*1024*10));
                            ch.pipeline().addLast(new HttpServerHandler());  
                        };  
                        
                    }).option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)  
             // 绑定端口，开始接收进来的连接  
             ChannelFuture future = sbs.bind(port).sync();    
             
             LOGGER.info("http server start listen at " + port );
             future.channel().closeFuture().sync();  
        } catch (Exception e) {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }	
    
    public static void main(String[] args) {
		new HttpServer(80).start();
	}
}  