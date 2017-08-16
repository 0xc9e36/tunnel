import com.tunnel.common.TunnelBaseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends TunnelBaseHandler {
    private Client client;
    public ClientHandler(Client client) {
		super("S2C");
        this.client = client;
    }


	@Override
	protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf buf) {
		byte[] data = new byte[buf.readableBytes()];
    	buf.getBytes(buf.readerIndex(), data,0,data.length);
        String content = new String(data);
        System.out.println(content);
	}
    
    @Override
    protected void handleWriterIdle(ChannelHandlerContext ctx) {
    	//客户端超过时间没有发送消息，发个心跳包以维持服务端连接
        sendPingMsg(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	//链路发现关闭了，需要重连
        client.doConnect();
    }

}