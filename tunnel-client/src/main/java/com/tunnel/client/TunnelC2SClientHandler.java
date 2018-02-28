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
	protected void handleData(ChannelHandlerContext ctx, ByteBuf buf, byte flag) {
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