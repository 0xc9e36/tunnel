package com.tunnel.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.TunnelBaseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class TunnelC2SClientHandler extends TunnelBaseHandler {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private TunnelC2SClient tunnelC2SClient;
	
	public TunnelC2SClientHandler(TunnelC2SClient tunnelC2SClient) {
		super("C2S");
		this.tunnelC2SClient = tunnelC2SClient;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		//连接成功，设置全局对象
		TunnelC2SManager.setChannelHandlerContext(ctx);
	}

	@Override
	protected void handleData(ChannelHandlerContext ctx, ByteBuf buf) {
		//只发送，不接受
	}

	@Override
	protected void handleWriterIdle(ChannelHandlerContext ctx) {
		//心跳
		super.handleWriterIdle(ctx);
		sendPingMsg(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//断线重连
		super.channelInactive(ctx);
		tunnelC2SClient.connect();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		LOGGER.error("",e);
		ctx.close();
	}
}