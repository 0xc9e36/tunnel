package com.tunnel.server.core;

import java.nio.channels.SocketChannel;

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
	 * 客户端名称
	 */
	private String clientName;
	
	private SocketChannel notifyChannel;
	
	private SocketChannel dataChannel;
	
	public Tunnel(String host, String clientName) {
		this.host = host;
		this.clientName = clientName;
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

	public SocketChannel getNotifyChannel() {
		return notifyChannel;
	}

	public void setNotifyChannel(SocketChannel notifyChannel) {
		this.notifyChannel = notifyChannel;
	}

	public SocketChannel getDataChannel() {
		return dataChannel;
	}

	public void setDataChannel(SocketChannel dataChannel) {
		this.dataChannel = dataChannel;
	}

}
