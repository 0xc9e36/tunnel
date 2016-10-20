package com.tunnel.server;

import java.io.OutputStream;

import io.netty.channel.Channel;

public class Tunnel {
	
	private Channel channel;
	private OutputStream out;
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public OutputStream getOut() {
		return out;
	}
	public void setOut(OutputStream out) {
		this.out = out;
	}
}
