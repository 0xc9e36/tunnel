package com.tunnel.client;

public class CRun {
	public static void main(String[] args) {
		String configFileUrl = "sys.properties";
		if(args != null && args.length > 0){
			configFileUrl = args[0];
		}
		
		Config.init(configFileUrl);
		
		new TunnelS2CClient().start();
		new TunnelC2SClient().start();
		
	}
}
