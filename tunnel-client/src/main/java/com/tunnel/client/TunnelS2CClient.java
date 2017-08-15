package com.tunnel.client;
  
import com.tunnel.common.Constant;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;  
  
public class TunnelS2CClient extends Thread{
      
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
                     //请求内容不超过10M
                     ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024*1024*10,Unpooled.copiedBuffer(Constant.ENT_FLAG.getBytes())));
                 	 p.addLast(new StringEncoder());//把字符串消息编码成字节码传输
                     p.addLast(new TunnelS2CClientHandler());
                 }
             });

            ChannelFuture future = b.connect(Config.SERVER_IP, Config.REGISTER_PORT).sync();  
            future.channel().closeFuture().sync();  
        } catch (Exception e) {
			e.printStackTrace();
		} finally {  
            group.shutdownGracefully();  
        }
    }
    
}