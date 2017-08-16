package com.tunnel.client;

public class CRun {
	public static void main(String[] args) {
		new TunnelS2CClient().start();
		new TunnelC2SClient().start();
		
	}
}
