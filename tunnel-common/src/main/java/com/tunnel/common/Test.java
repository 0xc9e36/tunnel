package com.tunnel.common;

public class Test {
	public static void main(String[] args) {
		byte[] data = ("HTTP/1.1 200 OK\r\n"
				     + "Content-Type: text/plain\r\n\r\n"
				     + "25\r\n"
				     + "This is the data in the first chunk\r\n"
				     + "1C\r\n"
				     + "and this is the second one\r\n"
				     + "3\r\n"
				     + "con\r\n"
				     + "8\r\n"
				     + "sequence\r\n"
				     + "0\r\n"
				     + "\r\n").getBytes();
		HttpData result = HttpUtil.analyzeHttpData(data);
		System.out.println(new String(result.getData()));
		System.out.println(result.isOk());
	}
}
