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
package com.tunnel.server;

import io.netty.channel.ChannelHandlerContext;

/**
 * 客户的通讯工具
 * 客户端
 */
public class Tunnel {

	/**
	 * 域名，唯一的
	 */
	private String host;
	
	/**
	 * 域名在tunnel_client那边的下标，通讯时候需要传递这个下标
	 */
	private int hostIndex;
	
	/**
	 * 客户端名称
	 */
	private String clientName;
	
	private ChannelHandlerContext channelHandlerContext;
	
	public Tunnel(String host, String clientName, int hostIndex) {
		this.host = host;
		this.clientName = clientName;
		this.hostIndex = hostIndex;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public ChannelHandlerContext getChannelHandlerContext() {
		return channelHandlerContext;
	}

	public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
		this.channelHandlerContext = channelHandlerContext;
	}

	public int getHostIndex() {
		return hostIndex;
	}

	public void setHostIndex(int hostIndex) {
		this.hostIndex = hostIndex;
	}
}
