package com.tunnel.client.thread;

import com.tunnel.client.core.Worker;
import com.tunnel.common.HttpData;

/**
 * 处理服务端命令的线程
 */
public class AskThread extends Thread{
	
	private byte[] data = null;
	/**
	 * 这是主要的工作者，负责响应取件命令、派送数据、反馈服务端
	 */
	private Worker worker = new Worker();

	public AskThread(byte[] data) {
		this.data = data;
		this.setName("AskThread");
	}
	
	@Override
	public void run() {
		String result = new String(data);
		result = result.substring("#_ASK-#".length());
		//格式是  包裹id#_SPLIT-#host
		String[] split = result.split("#_SPLIT-#");
		try {
			
			//1.按照包裹id，去服务端取件
			byte[] pack = worker.pickup(split[0]);
//			System.out.println("=======pack======");
//			System.out.println(new String(pack));
			//2.将包裹送达到终端并处理
			HttpData reply = worker.send(split[1], pack);
			//3.将出来结果发送给服务端
			if(reply != null){
//				System.out.println("=======reply header======");
//				System.out.println(new String(reply.getHeader()));
				
				worker.reply(split[0], reply);
			}else{
				worker.reply(split[0], "404 resource not found (from host)".getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
			//3.将出来结果发送给服务端
			worker.reply(split[0], ("500 server error "+e.getMessage()).getBytes());
		}
	}
}
