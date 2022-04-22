package com.wang.netty.InBoundHandlerAndOutBoundHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

        System.out.println("服务器的ip="+ ctx.channel().remoteAddress());
        System.out.println("收到服务器消息="+msg);

    }

    //重写channelActive发送数据

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler发送数据");
        ctx.writeAndFlush(123456L);//发送了一个long
        /**
         * ①abcdabcdabcdabcd 有16个字节
         * ②给处理器的前一个handler是MyLongToByteEncoder
         * ③MyLongToByteEncoder 的父类是 MessageToByteEncoder
         *
         * 在编写encoder是要注意传入的数据类型要和处理的数据类型一致
         * */
//        ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd", CharsetUtil.UTF_8));
    }
}
