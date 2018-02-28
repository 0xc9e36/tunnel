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
package com.tunnel.client;
  
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.Constant;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;  
/**
 * S->C通讯管道
 * 初始连接时候，负责客户端注册
 * 注册成功后，负责启动汇报管道
 * 全部启动完成后，开始接受请求
 */
public class TunnelS2CClient{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private EventLoopGroup group = new NioEventLoopGroup();  
	private Bootstrap  bootstrap;
    private Channel channel;
    private int connectFailedTimes = 0;
      
    //bootstrap配置好
    public void start(){
        try {
        	bootstrap = new Bootstrap();;
        	bootstrap.group(group)
             .channel(NioSocketChannel.class)
//             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     //5秒内没有数据发送，就需要发送一个心跳包维持服务端的连接
                     //由于此管道，除了在连接之初会汇报注册信息，剩余时间都没有数据上传，因此会每5秒一次心跳
                     p.addLast(new IdleStateHandler(0, 5, 0));
                     //请求内容不超过10M
                     p.addLast(new DelimiterBasedFrameDecoder(1024*1024*10,Unpooled.copiedBuffer(Constant.ENT_FLAG_BYTES)));
                     p.addLast(new TunnelS2CClientHandler(TunnelS2CClient.this));
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

        ChannelFuture future = bootstrap.connect(Config.SERVER_IP, Config.REGISTER_PORT);  
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    connectFailedTimes = 0;//清空失败的连接次数
                    LOGGER.info("S2C server Connect successfully!");
                } else {
                	connectFailedTimes++;
                    LOGGER.info("Failed to connect S2C server x"+connectFailedTimes+", try connect after 10s");
                    if(connectFailedTimes > 3){
                    	//超过三次，放弃连接，全部关闭
                    	LOGGER.info("s2c shutdown");
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