package com.tunnel.client;
  
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;  
  
public class TunnelC2SClient extends Thread{
    @Override
    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();  
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(new TunnelC2SClientHandler());
                 }
             });

            ChannelFuture future = b.connect(Config.SERVER_IP, Config.REPLY_PORT).sync();  
            future.channel().closeFuture().sync();  
        } catch (Exception e) {
			e.printStackTrace();
		} finally {  
            group.shutdownGracefully();  
        }
    }
    
}