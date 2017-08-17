import com.tunnel.common.TunnelBaseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends TunnelBaseHandler {
    public ServerHandler() {
		super("S2C");
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf buf, byte flag) {
    	byte[] data = new byte[buf.readableBytes()];
    	buf.getBytes(buf.readerIndex(), data,0,data.length);
        String content = new String(data);
        System.out.println(content);
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        ctx.close();
    }
}