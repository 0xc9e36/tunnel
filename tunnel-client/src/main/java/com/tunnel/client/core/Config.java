package com.tunnel.client.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.PropsUtil;
import com.tunnel.common.StringUtil;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

	public static String SERVER_IP;
	public static int REGISTER_PORT;
	public static int PICKUP_PORT;
	public static int REPLY_PORT;

	public static String NAME;
	public static String HOST_ARY;
	
	public static Map<String,IpAndPort> HOST_MAP = new HashMap<>();
	static {
		Properties props = PropsUtil.loadProps("sys.properties");
		SERVER_IP = PropsUtil.getString(props, "server_ip");
		if (StringUtil.isEmpty(SERVER_IP)) {
			LOGGER.error("服务器[IP]配置错误：server_ip=" + SERVER_IP);
			System.exit(0);
		}

		REGISTER_PORT = PropsUtil.getInt(props, "register_port");
		if (REGISTER_PORT <= 0) {
			LOGGER.error("注册[端口]配置错误：server_port=" + REGISTER_PORT);
			System.exit(0);
		}
		
		PICKUP_PORT = PropsUtil.getInt(props, "pickup_port");
		if (PICKUP_PORT <= 0) {
			LOGGER.error("取件[端口]配置错误：pick_port=" + PICKUP_PORT);
			System.exit(0);
		}

		REPLY_PORT = PropsUtil.getInt(props, "reply_port");
		if (REPLY_PORT <= 0) {
			LOGGER.error("汇报[端口]配置错误：reply_port=" + REPLY_PORT);
			System.exit(0);
		}
		
		NAME = PropsUtil.getString(props, "name");
		if (StringUtil.isEmpty(NAME)) {
			LOGGER.error("客户端[名称]配置错误：name=" + NAME);
			System.exit(0);
		}

		HOST_ARY = PropsUtil.getString(props, "host_ary");
		if(HOST_ARY != null) HOST_ARY=HOST_ARY.trim();
		if (StringUtil.isEmpty(HOST_ARY)) {
			LOGGER.error("客户端[域名列表]配置错误：host_ary=" + HOST_ARY);
			System.exit(0);
		}
		
		//查看域名和地址的映射
		String[] hostAry = HOST_ARY.split(",");
		for(String host:hostAry){
			//检查域名配置
			if(host != null) host=host.trim();
			if(StringUtil.isEmpty(host)){
				LOGGER.error("客户端[域名列表]配置错误：host_ary=" + HOST_ARY);
				System.exit(0);
			}
			
			//检查域名映射配置
			String mapper = PropsUtil.getString(props, host);
			if(mapper != null) mapper = mapper.trim();
			if(StringUtil.isEmpty(mapper)){
				LOGGER.error("客户端[域名映射]配置错误："+host+"=" + mapper);
				System.exit(0);
			}
			String[] ipAndPort = mapper.split(":");
			if(ipAndPort.length != 2){
				LOGGER.error("客户端[域名映射]配置错误（提示 ip:端口）："+host+"=" + mapper);
				System.exit(0);
			}
			
			try {
				IpAndPort ipAndPort2 = new IpAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
				HOST_MAP.put(host, ipAndPort2);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("客户端[域名映射]配置错误（提示 ip:端口）："+host+"=" + mapper);
				System.exit(0);
			}
		}
	}
}
