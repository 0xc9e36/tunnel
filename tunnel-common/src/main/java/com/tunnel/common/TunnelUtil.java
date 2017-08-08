package com.tunnel.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class TunnelUtil {
	private static final byte[] END_BYTE_ARY = "#_EOF-#".getBytes();
	
	public static List<byte[]> receiveData(SocketChannel socketChannel) throws IOException {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        byte[] data = new byte[0];
        List<byte[]> result = new ArrayList<>();
        try {  
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);  
            int len = 0;  
            while ((len = socketChannel.read(buffer)) >= 0) {  
                buffer.flip();
                byte[] bytes = new byte[len];  
                buffer.get(bytes);  
                baos.write(bytes);  
                buffer.clear();  
                data = baos.toByteArray();
            	if(endWith(data, END_BYTE_ARY)){
            		break;
            	}
            }
            data = baos.toByteArray();
            result = analyzeByteArayData(data);
            
//            socketChannel.socket().shutdownInput();  
        } finally {  
            try {  
                baos.close();  
            } catch(Exception ex) {}  
        }  
        return result;  
    }
	
	public static List<byte[]> analyzeByteArayData(byte[] data){
		//数据中可能包含多个数据包
		//每个数据包都以结束符作为结束
		List<byte[]> result = new ArrayList<>();
		for(int i=0,position=0;i<data.length;i++){
			if(data[i] == END_BYTE_ARY[0]){
				//从这开始比对
				int j=0;
				for(;j<END_BYTE_ARY.length;i++,j++){
					if(data[i] != END_BYTE_ARY[j]){
						break;
					}
				}
				if(j == END_BYTE_ARY.length){
					//找到完全匹配了，开始拿数据包
					byte[] pack = new byte[i-j-position];
					System.arraycopy(data, position, pack, 0, pack.length);
					result.add(pack);
					position = i;
				}
				i--;
			}
		}
		return result;
	}
	
	
	public static void sendData(SocketChannel socketChannel, byte[] data) throws IOException {  
        ByteBuffer buffer = ByteBuffer.wrap(data);
        socketChannel.write(buffer);
//        socketChannel.socket().shutdownOutput();  
    }
	
	public static void sendEnd(SocketChannel socketChannel) throws IOException{
		ByteBuffer buffer = ByteBuffer.wrap(END_BYTE_ARY);
        socketChannel.write(buffer);
	}
	
	public static boolean startWith(byte[] data,String startWith){
		byte[] bytes = startWith.getBytes();
		return startWith(data, bytes);
	}
	
	public static boolean startWith(byte[] data,byte[] startWith){
		if(startWith.length > data.length){
			return false;
		}
		for(int i=0;i<startWith.length;i++){
			if(startWith[i] != data[i]){
				return false;
			}
		}
		return true;
	}
	
	public static boolean endWith(byte[] data,byte[] endWith){
		if(endWith.length > data.length){
			return false;
		}
		for(int i=endWith.length-1,j=data.length-1;i>=0 && j>=0;i--,j--){
			if(endWith[i] != data[j]){
				return false;
			}
		}
		return true;
	}
}
