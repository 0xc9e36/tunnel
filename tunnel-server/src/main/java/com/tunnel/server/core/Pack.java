package com.tunnel.server.core;

import com.tunnel.common.HttpData;

/**
 * 包裹
 */
public class Pack {

	/**
	 * 包裹数据
	 */
	private HttpData request;
	
	/**
	 * 包裹处理结果
	 * response和request不一样，
	 * 因为response其实来自tunnel-client的数据，走的tunnel通道，可以明确判断出结尾，
	 * 而request是httpserver的数据，来自浏览器，需要特殊处理判断，并且有对请求头的特殊处理。
	 */
	private byte[] response;
	
	public Pack(HttpData request) {
		this.request = request;
	}

	public HttpData getRequest() {
		return request;
	}

	public void setRequest(HttpData request) {
		this.request = request;
	}

	public byte[] getResponse() {
		return response;
	}

	public void setResponse(byte[] response) {
		this.response = response;
	}
	
}
