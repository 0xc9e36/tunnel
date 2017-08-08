package com.tunnel.client.thread;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.tunnel.common.StringUtil;
import com.tunnel.common.TunnelUtil;

/**
 * 客户端线程
 * 处理所有在注册以后的数据交互
 */
public class SwitchThread extends Thread{

	/**
	 * 这根管道，是长连接，维系客户端和服务端的命令通讯
	 */
	private SocketChannel socketChannel;
	
	public SwitchThread(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void run() {
		while(true){
			try {
				//读取来自于server的连接管道内的消息包
				List<byte[]> receiveData = TunnelUtil.receiveData(socketChannel);
				for(byte[] data:receiveData){
					boolean isRegister = TunnelUtil.startWith(data, "#_REGISTER-#");
					if(isRegister){
						doRegister(data);
						continue;
					}
					boolean isHeart = TunnelUtil.startWith(data, "#_HEART-#");
					if(isHeart){
						doHeart(data);
						continue;
					}
					boolean isAsk = TunnelUtil.startWith(data, "#_ASK-#");
					if(isAsk){
						new AskThread(data).start();
						continue;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	private void doRegister(byte[] data){
		String result = new String(data);
		result = result.substring("#_REGISTER-#".length());
		
        if(StringUtil.isNotEmpty(result) && result.indexOf("#_SPLIT-#") > 0){
        	String[] split = result.split("#_SPLIT-#");
        	System.out.println("注册成功："+split[0]);
        	System.out.println("可以开始访问了！");
        	if(split.length > 1){
            	System.out.println("注册失败："+split[1]);
        	}
        }else{
        	System.out.println("注册失败");
        	System.exit(0);
        }
	}
	
	private void doHeart(byte[] data){
		//心跳
	}
	
}
