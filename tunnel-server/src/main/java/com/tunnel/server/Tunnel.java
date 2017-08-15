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
