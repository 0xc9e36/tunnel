package com.tunnel.server;

public class SRun {

	public static void main(String[] args) {
		new HttpServer(Config.HTTP_SERVER_PORT).start();
		new TunnelS2CServer().start();
		new TunnelC2SServer().start();
	}
}
