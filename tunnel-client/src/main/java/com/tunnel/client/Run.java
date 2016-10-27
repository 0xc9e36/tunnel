package com.tunnel.client;

public class Run {

	public static void main(String[] args) {
		try {
//			TunnelClient.main(new String[]{"frontend","localhost","89"});
			TunnelClient.main(new String[]{"gaspipe","localhost","8082"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
