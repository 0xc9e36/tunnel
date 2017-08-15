package test;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;  
  
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {  
    private static Logger   logger  = LoggerFactory.getLogger(HttpServerInboundHandler.class);  
  
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
    	FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("I am ok"  
                .getBytes()));  
        response.headers().set(CONTENT_TYPE, "text/plain");  
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());  
        response.headers().set(CONNECTION, Values.KEEP_ALIVE);
        ctx.write(response);  
        ctx.flush();  
    }  
  
    @Override  
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
        logger.info("HttpServerInboundHandler.channelReadComplete");  
        ctx.flush();  
    }  
  
}  