package com.tunnel.client;

import java.io.IOException;
import java.io.InputStream;
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
		int start=-1,end=-1;
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
		if(end>start && start>=0 && end>=0){
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
	
	public static byte[] replaceLast(byte[] data,String reg,String str){
		
		StringBuilder find = new StringBuilder();
		int start=-1,end=-1;
		for(int i=data.length-1;i>=0;i--){
			char c = (char)data[i];
			if(reg.endsWith(c+"") && find.length() == 0){
				find.insert(0, c);
				end = i;
			}else{
				if(find.length() > 0){
					find.insert(0,c);
					if(reg.endsWith(find.toString())){
						if(reg.equals(find.toString())){
							start = i;
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
		if(end>start && start>=0 && end>=0){
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
	
	
	public static byte[] readData(InputStream in){
		try {
			if(in.available() > 0){
				byte[] result = new byte[0];
				int index = 0;
				byte[] data = new byte[1024];
				int len = 0;
				while((len=in.read(data, 0, data.length)) > 0){
					if(index+len > result.length){
						byte[] newResult = new byte[result.length+1024];
						System.arraycopy(result, 0, newResult, 0, result.length);
						result = newResult;
						
					}
					System.arraycopy(data, 0, result, index, len);
					index = index+len;
				}
				
				if(index <= result.length){
					//È¥Î²²¿¿Õ
					byte[] newResult = new byte[index];
					System.arraycopy(result, 0, newResult, 0, index);
					result = newResult;
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
//		System.out.println(filterEnd("abcds#<t>".getBytes()));
//		System.out.println(filterEnd("!end_/@".getBytes()));
		
		byte[] data = replaceLast("POST /v1/base/employee/login HTTP/1.1 Host: localhost:8082 Connection: Close".getBytes(), "Connection: Close", "");
		StringBuilder buf = new StringBuilder();
		for(byte b:data){
			buf.append((char)b);
		}
		System.out.println(buf.toString());
	}
}
