package com.tunnel.server;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.PropsUtil;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    public static int HTTP_SERVER_PORT;
    public static int REGISTER_PORT;
	public static int REPLY_PORT;

	public static void init(String configFileUrl){
		Properties props = PropsUtil.loadProps(configFileUrl);
		if(props == null){
			LOGGER.error("配置文件不存在："+configFileUrl);
			System.exit(0);
		}
		HTTP_SERVER_PORT = PropsUtil.getInt(props, "http_server_port");
		if(HTTP_SERVER_PORT <= 0){
			LOGGER.error("http服务[端口]配置错误：http_server_port="+HTTP_SERVER_PORT);
			System.exit(0);
		}
		
		REGISTER_PORT = PropsUtil.getInt(props, "register_port");
		if(REGISTER_PORT <= 0){
			LOGGER.error("客户端注册[端口]配置错误：register_port="+REGISTER_PORT);
			System.exit(0);
		}

		REPLY_PORT = PropsUtil.getInt(props, "reply_port");
		if (REPLY_PORT <= 0) {
			LOGGER.error("汇报[端口]配置错误：reply_port=" + REPLY_PORT);
			System.exit(0);
		}
	}
	
}
