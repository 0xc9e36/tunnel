package com.tunnel.server.thread;

import com.tunnel.server.core.TunnelManager;

/**
 * 保活线程
 * 负责如下事务：
 * 监视客户端是否存活，如果已经断开，移除ClientBox内的客户
 */
public class KeepAliveThread extends Thread{
	@Override
	public void run() {
		while(true){
			TunnelManager.removeOneDied();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}
