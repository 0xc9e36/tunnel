package com.tunnel.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.Constant;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;  
  
public class TunnelC2SServer extends Thread{  
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
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
                        	ChannelPipeline p = ch.pipeline();
                        	//15秒钟心跳检测，客户端5秒发一次心跳，如果15秒没收到，断开连接
                        	p.addLast(new IdleStateHandler(15, 0, 0));
                            //返回内容，不超过10M
                        	p.addLast(new DelimiterBasedFrameDecoder(1024*1024*10,Unpooled.copiedBuffer(Constant.ENT_FLAG_BYTES)));
                        	p.addLast(new TunnelC2SServerHandler()); 
                        };  
                        
                    }).option(ChannelOption.SO_BACKLOG, 128)     
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  
             // 绑定端口，开始接收进来的连接  
             ChannelFuture future = sbs.bind(Config.REPLY_PORT).sync();    
             
             LOGGER.info("tunnel c2s server start listen at " + Config.REPLY_PORT);
             future.channel().closeFuture().sync();  
        } catch (Exception e) {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }	
}  