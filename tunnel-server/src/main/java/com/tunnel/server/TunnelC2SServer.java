package com.tunnel.server;

import com.tunnel.common.Constant;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;  
  
public class TunnelC2SServer extends Thread{  
  
      
    public TunnelC2SServer() {  
        this.setName("TunnelC2SServer thread");
    }  
    
    @Override
    public void run(){  
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {  
            ServerBootstrap sbs = new ServerBootstrap()
            		.group(bossGroup,workerGroup)
            		.channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)  
                    .childHandler(new ChannelInitializer<SocketChannel>() {  
                        
                        protected void initChannel(SocketChannel ch) throws Exception {
                        	ch.pipeline().addLast(new HttpResponseEncoder());
                        	//返回内容，不超过10M
                        	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024*1024*10,Unpooled.copiedBuffer(Constant.ENT_FLAG.getBytes())));
                        	ch.pipeline().addLast(new TunnelC2SServerHandler()); 
                        };  
                        
                    }).option(ChannelOption.SO_BACKLOG, 128)     
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  
             // 绑定端口，开始接收进来的连接  
             ChannelFuture future = sbs.bind(Config.REPLY_PORT).sync();    
             
             System.out.println("tunnel c2s server start listen at " + Config.REPLY_PORT );  
             future.channel().closeFuture().sync();  
        } catch (Exception e) {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }	
}  