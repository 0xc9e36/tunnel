package com.tunnel.server;

import java.io.IOException;
import java.io.OutputStream;

public class HttpUtil {

	public static void endSend(OutputStream out){
		String flag = getFlag();
		byte[] data = flag.getBytes();
		try {
			out.write(data,0,data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String filterEnd(byte[] data){
		String flag = getFlag();
		StringBuilder contentBuf = new StringBuilder();
		for(byte b:data){
			contentBuf.append((char)b);
		}
		String content = contentBuf.toString();
		StringBuilder flagBuf = new StringBuilder();
		String flagPic = "NOTEND";
		byte[] flagByte = flag.getBytes();
		for(int i=0;i<flagByte.length;i++){
			for(int j=i;j<flagByte.length;j++){
				flagBuf.setLength(0);
				for(int m=i;m<=j;m++){
					flagBuf.append((char)flagByte[m]);
				}
				if(content.startsWith(flagBuf.toString()) || content.endsWith(flagBuf.toString())){
					if(!flagPic.contains(flagBuf.toString())){
						flagPic = flagBuf.toString();
					}
				}
			}
		}
		return flagPic;
	}
	
	public static String getFlag(){
		String flag = "#<t>!end_/@";
		return flag;
	}
	
	public static void logData(byte[] data){
		StringBuilder buf = new StringBuilder();
		for(byte b:data){
			buf.append((char)b);
		}
		System.out.println(buf.toString());
	}
	
	public static byte[] replaceFirst(byte[] data,String reg,String str){
		
		StringBuilder find = new StringBuilder();
		int start = 0,end=0;
		for(int i=0;i<data.length;i++){
			char c = (char)data[i];
			if(reg.startsWith(c+"") && find.length() == 0){
				find.append(c);
				start = i;
			}else{
				if(find.length() > 0){
					find.append(c);
					if(reg.startsWith(find.toString())){
						if(reg.equals(find.toString())){
							end = i;
							break;
						}
					}else{
						start = 0;
						end = 0;
						find.setLength(0);
					}
				}
			}
		}
		if(end>start){
			int jianLen = end-start+1;
			int addLen = str.length();
			byte[] newData = new byte[data.length-jianLen+addLen];
			System.arraycopy(data, 0, newData, 0, start);//(1)
			byte[] replaceByte = str.getBytes();
			System.arraycopy(replaceByte, 0, newData, start, addLen);//(2)
			System.arraycopy(data, end+1, newData, start+addLen, newData.length-(start+addLen));//(3)
			
			data = newData;
		}
		
		return data;
	}
	
	public static void main(String[] args) {
//		System.out.println(filterEnd("abcds#<t>".getBytes()));
//		System.out.println(filterEnd("!end_/@".getBytes()));
		
		byte[] data = replaceFirst("/gaspipe/v1/ok".getBytes(), "gaspipe/", "");
		StringBuilder buf = new StringBuilder();
		for(byte b:data){
			buf.append((char)b);
		}
		System.out.println(buf.toString());
	}
}
