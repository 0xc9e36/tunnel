/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tunnel.server;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tunnel.common.TunnelBaseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;  
  
public class TunnelC2SServerHandler extends TunnelBaseHandler{  
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
    public TunnelC2SServerHandler() {
		super("C2S");
	}

	@Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
    	cause.printStackTrace();  
        ctx.close();
    }

	@Override
	protected void handleData(ChannelHandlerContext ctx, ByteBuf buf, byte flag) {
		//前16个字符是
		//requestid 时间戳+三位随机数，代表http请求的编号 占16位
		if(buf.readableBytes() > 16){
			byte[] requestidBytes = new byte[16];
			buf.getBytes(buf.readerIndex(), requestidBytes, 0, 16);
			
			byte[] dataBytes = new byte[buf.readableBytes()-16];
			buf.getBytes(buf.readerIndex()+16, dataBytes, 0, dataBytes.length);
			
			//解析完数据接口
			//开始查找转发地址，转发数据
			ChannelHandlerContext httpCtx = HttpChannelManager.get(new String(requestidBytes));
			if(httpCtx != null){
				httpCtx.writeAndFlush(dataBytes).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}
	

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        ctx.close();
        LOGGER.info("关闭C2S连接");
    }
	
}  