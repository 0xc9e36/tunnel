package com.tunnel.server;
  
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;  
  
public class TunnelC2SServerHandler extends SimpleChannelInboundHandler<ByteBuf>{  
    
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
    	cause.printStackTrace();  
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		//前16个字符是
		//requestid 时间戳+三位随机数，代表http请求的编号 占16位
		if(buf.readableBytes() > 16){
			byte[] requestidBytes = new byte[16];
			buf.getBytes(buf.readerIndex(), requestidBytes, 0, 16);
			
			byte[] dataBytes = new byte[buf.readableBytes()-16];
			buf.getBytes(buf.readerIndex()+16, dataBytes, 0, dataBytes.length);
			
			//解析完数据接口
			//开始查找转发地址，转发数据
			ChannelHandlerContext httpCtx = HttpChannelManager.get(new String(requestidBytes));
			if(httpCtx != null){
				httpCtx.writeAndFlush(dataBytes).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}
	
}  