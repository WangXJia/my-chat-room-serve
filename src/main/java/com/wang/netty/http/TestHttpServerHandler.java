package com.wang.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * 说明：
 * ①SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter 的子类
 * ②HttpObject表示客户端和服务器端相互通讯的数据被封装成HttpObject类型
 *
 * */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    //channelRead0 读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //判断msg是不是一个httpRequest请求
        if (msg instanceof HttpRequest){
            System.out.println("msg 类型="+msg.getClass());
            System.out.println("客户端地址: "+ctx.channel().remoteAddress());

            //获取到
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取uri
            URI uri = new URI(httpRequest.uri());
            //过滤
            if ("/favicon.ico".equals(uri.getPath())){
                System.out.println("请求了/favicon.ico，不做响应");
                return;
            }

            //回复信息给浏览器[满足http协议]
            ByteBuf content = Unpooled.copiedBuffer("hello,我是服务器", CharsetUtil.UTF_8);
            //构造一个HTTP的响应，即httpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            //设置头
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=utf-8;");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            //将构建好的response返回
            ctx.writeAndFlush(response);
        }
    }
}
