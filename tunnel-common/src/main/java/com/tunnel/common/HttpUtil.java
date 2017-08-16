package com.tunnel.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpUtil {

	 //有本事不要格式化
	 public static HttpData readData(InputStream in) throws IOException{
	 ByteArrayOutputStream baos = new ByteArrayOutputStream(); HttpData
	 httpData = null; try { //TODO:1024字节够不够完全描述一次http请求，这里还要具体分析 
	 byte[] data = new byte[1024]; int len = 0; while ((len = in.read(data)) >= 0) {
	 baos.write(data,0,len); httpData = analyzeHttpData(baos.toByteArray());
	 if(httpData.isOk()) break; } if(httpData != null && httpData.getData() !=
	 null) httpData.setOk(true); } finally { try { baos.close(); }
	 catch(Exception ex) {} } return httpData; }
	 

	public static HttpData analyzeHttpData(byte[] data) {
		// 数据中可能包含多个数据包
		// 每个数据包都以结束符作为结束
		byte[] headerEndFlag = "\r\n\r\n".getBytes();
		HttpData httpData = null;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == headerEndFlag[0]) {
				// 从这开始比对
				int j = 0;
				for (; j < headerEndFlag.length; i++, j++) {
					if (data[i] != headerEndFlag[j]) {
						break;
					}
				}
				if (j == headerEndFlag.length) {
					// 找到完全匹配了，开始拿数据包
					byte[] header = new byte[i];
					System.arraycopy(data, 0, header, 0, header.length);
					httpData = new HttpData();
					httpData.setHeader(header);
					break;
				}
			}
		}

		if (httpData != null && httpData.getHeader() != null) {
			// 根据头信息，分析数据的结构
			// if T-E: chunked, 就读, 直到流里有\r\n0\r\n\r\n
			// else if Content-Length存在, 就从头的末尾开始计算C-L个字节.
			// else 就这么一直读等服务器断开连接就好.
			byte[] header = httpData.getHeader();

			String content = new String(header);
			if (content.indexOf("Transfer-Encoding:") > 0) {
				byte[] bodyEndFlag = "\r\n0\r\n\r\n".getBytes();
				for (int i = header.length; i < data.length; i++) {
					if (data[i] == bodyEndFlag[0]) {
						// 从这开始比对
						int j = 0;
						for (; j < bodyEndFlag.length; i++, j++) {
							if (data[i] != bodyEndFlag[j]) {
								break;
							}
						}
						// i+2>data.length完全是因为，尽量避免数据包中出现和结束符一样的特殊情况，2是大概给的
						// 只要能判断出包确实接近结尾了就好
						if (j == bodyEndFlag.length && i + 2 > data.length) {
							// 找到完全匹配了，开始拿数据包
							byte[] body = new byte[i - header.length];
							System.arraycopy(data, header.length, body, 0, body.length);
							httpData.setData(body);
							httpData.setOk(true);
							break;
						}
					}
				}
			} else if (content.indexOf("Content-Length:") > 0) {
				int index = content.indexOf("Content-Length:");
				if (index > 0) {
					int end = content.indexOf("\r\n", index);
					String cl = content.substring(index + "Content-Length:".length(), end);
					int contentLength = Integer.parseInt(cl.trim());
					int bodyLength = data.length - header.length;
					if (bodyLength >= contentLength) {
						byte[] body = new byte[contentLength];
						System.arraycopy(data, header.length, body, 0, body.length);
						httpData.setData(body);
						httpData.setOk(true);
					}
				}
			} else {
				byte[] body = new byte[data.length - header.length];
				System.arraycopy(data, header.length, body, 0, body.length);
				httpData.setData(body);
				if (data.length < 1024) {
					httpData.setOk(true);
				}
			}
		}

		return httpData;
	}

	public static String getHost(byte[] header) {
		String content = new String(header);
		int index = content.indexOf("Host:");
		if (index > 0) {
			int end = content.indexOf("\r\n", index);
			String host = content.substring(index + "Host:".length(), end);
			return host.trim();
		}
		return "";
	}
	
	public static byte[] response400(String endPoint) {
		String content = "400 Bad Request(from "+endPoint+")";
		String http = "HTTP/1.1 400 Bad Request\r\n"+
		"Date: Sat, 31 Dec 2005 23:59:59 GMT\r\n"+
		"Content-Type: text/html;charset=ISO-8859-1\r\n"+
		"Connection: close\r\n"+
		"Content-Length: "+content.getBytes().length+"\r\n\r\n"+
		 content;
		return http.getBytes();
	}

	public static byte[] response404(String endPoint) {
		String content = "404 Not Found(from "+endPoint+")";
		String http = "HTTP/1.1 404 Not Found Request\r\n"+
		"Date: Sat, 31 Dec 2005 23:59:59 GMT\r\n"+
		"Content-Type: text/html;charset=ISO-8859-1\r\n"+
		"Connection: close\r\n"+
		"Content-Length: "+content.getBytes().length+"\r\n\r\n"+
		 content;
		return http.getBytes();
	}
	
	public static byte[] response500(String error,String endPoint) {
		String content = "500 Internal Server Error(from "+endPoint+"):"+error;
		String http = "HTTP/1.1 404 500 Internal Server Error\r\n"+
		"Date: Sat, 31 Dec 2005 23:59:59 GMT\r\n"+
		"Content-Type: text/html;charset=ISO-8859-1\r\n"+
		"Connection: close\r\n"+
		"Content-Length: "+content.getBytes().length+"\r\n\r\n"+
		 content;
		return http.getBytes();
	}
}
