package com.tunnel.client.core;

import java.nio.channels.SocketChannel;

import com.tunnel.client.thread.SwitchThread;

public class Run {

	public static void main(String[] args) {
		//注册到Server
		SocketChannel channel = new Register().excute();
		if(channel != null){
			new SwitchThread(channel).start();
		}
	}
}
