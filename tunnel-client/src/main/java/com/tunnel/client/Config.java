/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tunnel.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.PropsUtil;
import com.tunnel.common.StringUtil;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

	public static String SERVER_IP;
	public static int REGISTER_PORT;
	public static int REPLY_PORT;

	public static String NAME;
	public static String HOST_ARY;
	public static List<IpAndPort> HOST_LIST = new ArrayList<>();
	
	
	public static void init(String configFileUrl){
		
		Properties props = PropsUtil.loadProps(configFileUrl);
		if(props == null){
			LOGGER.error("配置文件不存在："+configFileUrl);
			System.exit(0);
		}
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
			String[] ipAndPortConfig = mapper.split(":");
			if(ipAndPortConfig.length != 2){
				LOGGER.error("客户端[域名映射]配置错误（提示 ip:端口）："+host+"=" + mapper);
				System.exit(0);
			}
			
			try {
				IpAndPort ipAndPort = new IpAndPort(ipAndPortConfig[0], Integer.parseInt(ipAndPortConfig[1]));
				HOST_LIST.add(ipAndPort);
			} catch (Exception e) {
				LOGGER.error("客户端[域名映射]配置错误（提示 ip:端口）："+host+"=" + mapper);
				System.exit(0);
			}
		}
	}
}
