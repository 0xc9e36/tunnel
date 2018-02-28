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
