package com.tunnel.server.core;

import com.tunnel.server.thread.HttpServerThread;
import com.tunnel.server.thread.KeepAliveThread;
import com.tunnel.server.thread.PickupThread;
import com.tunnel.server.thread.RegisterThread;
import com.tunnel.server.thread.ReplyThread;

/**
 * 总机，负责所有的人员安排
 */
public class Run {

	public static void main(String[] args) {
		//启动注册监听
		new RegisterThread().start();
		//启动通道保活监听
		new KeepAliveThread().start();
		//启动http服务端口
		new HttpServerThread().start();
		//启动客户端取件监听服务
		new PickupThread().start();
		//启动客户端反馈监听服务
		new ReplyThread().start();
	}
}
