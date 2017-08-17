package com.tunnel.client;
  
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;  
  
public class TunnelC2SClient{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private EventLoopGroup group = new NioEventLoopGroup();  
	private Bootstrap  bootstrap = new Bootstrap();;
    private Channel channel;
    private int connectFailedTimes = 0;
	
    public void start(){
        try {
        	bootstrap.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     //5秒内没有数据发送，就需要发送一个心跳包维持服务端的连接
                     p.addLast(new IdleStateHandler(0, 5, 0));
                     p.addLast(new TunnelC2SClientHandler(TunnelC2SClient.this));
                 }
             });
        	connect();
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void connect(){
        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture future = bootstrap.connect(Config.SERVER_IP, Config.REPLY_PORT);  
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    connectFailedTimes = 0;//清空失败的连接次数
                    LOGGER.info("C2S server Connect successfully!");
                } else {
                	connectFailedTimes++;
                    LOGGER.info("Failed to connect C2S server x"+connectFailedTimes+", try connect after 10s");
                    if(connectFailedTimes > 3){
                    	//超过三次，放弃连接，全部关闭
                    	LOGGER.info("c2s shutdown");
                    	group.shutdownGracefully();
                    }else{
                    	futureListener.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                            	connect();
                            }
                        }, 10, TimeUnit.SECONDS);
                    }
                }
            }
        });
    }
}