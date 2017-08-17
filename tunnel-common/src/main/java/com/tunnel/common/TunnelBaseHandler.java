package com.tunnel.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Tunnel的基础handler
 * 自带心跳机制
 */
public abstract class TunnelBaseHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	//ping消息
	public static final byte PING_MSG = 1;
    //客户消息
    public static final byte COMMON_MSG = 3;

    private String name;
    
    public TunnelBaseHandler(String name) {
    	this.name = name;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
    	byte flag = byteBuf.getByte(byteBuf.readerIndex());
    	if (flag == PING_MSG) {
            //心跳包什么都不做
    		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    		LOGGER.debug(name+" - "+sdf.format(new Date())+" get ping...");
        } else {
        	//除了心跳表，其余包都认为是数据包，
        	//要求数据包都要留存第一位为标志位
        	byteBuf.skipBytes(1);
            handleData(ctx, byteBuf, flag);
        }
    }

    protected ChannelFuture sendPingMsg(ChannelHandlerContext ctx) {
        //心跳包什么都不做
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		LOGGER.debug(name+" - "+sdf.format(new Date())+" ping...");
		
		return sendData(PING_MSG, ctx);
    }
    
    protected ChannelFuture sendData(byte flag,ChannelHandlerContext ctx,byte[] ...data){
    	int dataLen = 0;
    	for(byte[] dataOne:data){
    		dataLen = dataLen+dataOne.length;
    	}
    	ByteBuf buf = Unpooled.buffer(1+dataLen+Constant.ENT_FLAG_BYTES.length);  
        buf.writeByte(flag);//发送一个数据标识位
        for(byte[] dataOne:data){
        	buf.writeBytes(dataOne);//发送数据
        }
        buf.writeBytes(Constant.ENT_FLAG_BYTES);//发送结束标识
        return ctx.writeAndFlush(buf);
    }
    
    protected ChannelFuture sendCommonData(ChannelHandlerContext ctx,byte[] ...data){
    	return sendData(COMMON_MSG, ctx, data);
    }
    
    protected abstract void handleData(ChannelHandlerContext ctx, ByteBuf buf, byte flag);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {}

    protected void handleWriterIdle(ChannelHandlerContext ctx) {}

    protected void handleAllIdle(ChannelHandlerContext ctx) {}
}