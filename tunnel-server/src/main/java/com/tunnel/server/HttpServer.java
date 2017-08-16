package com.tunnel.server;

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
             
             System.out.println("http server start listen at " + port );  
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