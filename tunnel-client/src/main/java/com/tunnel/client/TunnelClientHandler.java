package com.tunnel.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class TunnelClientHandler extends ChannelInboundHandlerAdapter {
	private String tunnel = "gaspipe";
	private String host = "gaspipe.test.lngtop.com";
	private int port = 80;
	private final static int TIME_OUT = 10000;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		try {
			final ByteBuf time = ctx.alloc().buffer(100); // (2)
	        time.writeBytes(tunnel.getBytes());

	        ctx.writeAndFlush(time); // (3)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
        	StringBuilder request = new StringBuilder();
            while (in.isReadable()) {
            	request.append((char) in.readByte());
            }
            //解析并转发给目标服务
            String content = request.toString();
            content = content.replaceFirst(tunnel+"/", "");
            if(content.indexOf("Host: localhost:8010") > 0){
            	content = content.replaceFirst("Host: localhost:8010", "Host: "+host+":"+port);
            }
            content = content.replaceFirst("Connection: keep-alive", "Connection: Close");
            
            response(content.getBytes(), ctx.channel());
        
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    private boolean response(byte[] data,Channel channel){
		try {
			//向目标网站发送请求
			Socket server = new Socket(host,port);	
			server.setSoTimeout(TIME_OUT);
			OutputStream serverOut = server.getOutputStream();
			serverOut.write(data);
			serverOut.flush();
			
			//将目标网站返回的数据中转给客户端
			int n;
			byte[] buffer = new byte[1024];
			InputStream serverIn = server.getInputStream();
			ByteBuf byteBuf = channel.alloc().buffer();
			byteBuf.writeBytes((this.tunnel+"\r\n").getBytes());
			while((n=serverIn.read(buffer))!=-1){
				byteBuf.writeBytes(buffer,0,n);
			}
			serverOut.close();
			serverIn.close();
			server.close();
			
			Socket res = new Socket("localhost",8020);
			res.setSoTimeout(TIME_OUT);
			OutputStream resOut = res.getOutputStream();
			while(byteBuf.isReadable()){
				resOut.write(byteBuf.readByte());
			}
			resOut.flush();
			resOut.close();
			res.close();
			
			return true;
		} catch (IOException e) {
			//System.out.println("Exception:"+list.get(0));
			//e.printStackTrace();
			return false;
		}		
	}
}
