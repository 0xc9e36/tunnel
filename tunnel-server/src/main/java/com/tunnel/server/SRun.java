package com.tunnel.server;

public class SRun {

	public static void main(String[] args) {
		String configFileUrl = "sys.properties";
		if(args != null && args.length > 0){
			configFileUrl = args[0];
		}
		Config.init(configFileUrl);
		
		
		new HttpServer(Config.HTTP_SERVER_PORT).start();
		new TunnelS2CServer().start();
		new TunnelC2SServer().start();
	}
}
