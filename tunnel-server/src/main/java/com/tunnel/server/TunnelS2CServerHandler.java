package com.tunnel.server;
  
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.Constant;
import com.tunnel.common.StringUtil;
import com.tunnel.common.TunnelBaseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;  
  
public class TunnelS2CServerHandler extends TunnelBaseHandler{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public byte REGISTER_MSG = 5;
	
	public TunnelS2CServerHandler() {
		super("S2C");
	}

	public static final AttributeKey<String> CLIEN_HOSTS_KEY = AttributeKey.valueOf("client.host"); 
    
      
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        cause.printStackTrace(); 
        removeClient(ctx);
        ctx.close();
    }
    
	@Override
	protected void handleData(ChannelHandlerContext ctx, ByteBuf buf, byte flag) {
		byte[] data = new byte[buf.readableBytes()];
    	buf.getBytes(buf.readerIndex(), data,0,data.length);
        String content = new String(data);
		//注册信息
		if (StringUtil.isNotEmpty(content) || content.contains(Constant.SPLIT_FLAG)) {
			String[] split = content.split(Constant.SPLIT_FLAG);
			String clientName = split[0];
			String hostAry = split[1];
			String[] split2 = hostAry.split(",");

			List<String> successHostList = new ArrayList<>();
			List<String> failedHostList = new ArrayList<>();
			int hostIndex = 0;
			for (String host : split2) {
				host = host == null ? "" : host.trim();
				Tunnel tunnel = new Tunnel(host, clientName, hostIndex++);
				tunnel.setChannelHandlerContext(ctx);
				boolean ok = TunnelManager.add(tunnel);
				if (ok) {
					//如果客户的域名端登记注册成功
					//就把这个ctx里，记上对应的域名，因为在ctx异常或者关闭时候，tunnel是要被销毁的
					//tunnel的销毁，就靠ctx携带的域名来找
					Attribute<String> attr = ctx.attr(CLIEN_HOSTS_KEY);
					if(attr.get() == null){
						attr.setIfAbsent(hostAry);
					}
					successHostList.add(host);
				} else {
					failedHostList.add(host);
				}
			}
			
			
			//返回信息
			StringBuilder sb = new StringBuilder();
			sb.append("\r\n\r\n[REGISTER RESULT START]\r\n");
			//成功注册的域名
			sb.append("register success:\r\n");
			for (int i=0;i<successHostList.size();i++) {
				sb.append(i+1)
				.append("、")
				.append(successHostList.get(i))
				.append("\r\n");
			}
			sb.append("\r\n");
			//注册失败的域名
			sb.append("register failed:\r\n");
			for (int i=0;i<failedHostList.size();i++) {
				sb.append(i+1)
				.append("、")
				.append(failedHostList.get(i))
				.append("\r\n");
			}
			sb.append("\r\n");

			//现在服务端的域名信息
			sb.append("all host in server:\r\n");
			Collection<Tunnel> tunnels = TunnelManager.getTunnels();
			if(tunnels != null){
				int index=1;
				for(Tunnel tunnel:tunnels){
					sb.append(index++)
						.append("、")
						.append(tunnel.getClientName())
						.append("-")
						.append(tunnel.getHost())
						.append("\r\n");
				}
			}
			sb.append("[REGISTER RESULT END!]\r\n\r\n");
			
			sendData(REGISTER_MSG, ctx, sb.toString().getBytes());
			
		}
	}
	

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        removeClient(ctx);
        ctx.close();
        LOGGER.info("关闭S2C连接");
    }
    
    private void removeClient(ChannelHandlerContext ctx){
    	Attribute<String> attr = ctx.attr(CLIEN_HOSTS_KEY);
        if(attr.get() != null){
        	String hosts = attr.get();
        	String[] hostAry = hosts.split(",");
        	for(String host:hostAry){
        		TunnelManager.remove(host);
        	}
        }
    }

}  