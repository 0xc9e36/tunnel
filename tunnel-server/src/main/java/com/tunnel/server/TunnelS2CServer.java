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
import io.netty.handler.codec.string.StringDecoder;  
  
public class TunnelS2CServer extends Thread{  
  
    public TunnelS2CServer() {  
        this.setName("TunnelS2CServer thread");
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
                        	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024*1024,Unpooled.copiedBuffer(Constant.ENT_FLAG.getBytes())));
                        	ch.pipeline().addLast(new StringDecoder());
                        	ch.pipeline().addLast(new TunnelS2CServerHandler());  
                        };  
                        
                    }).option(ChannelOption.SO_BACKLOG, 128)     
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  
             // 绑定端口，开始接收进来的连接  
             ChannelFuture future = sbs.bind(Config.REGISTER_PORT).sync();    

             System.out.println("tunnel s2c server start listen at " + Config.REGISTER_PORT );  
             future.channel().closeFuture().sync();  
        } catch (Exception e) {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }	
}  