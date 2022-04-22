package com.wang.netty.webSocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TextWebSocketFrame类型表示一个文本帧（frame）
 * */
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //获取到当前channel
        Channel channel = ctx.channel();
        //这时我们遍历channelGroup，根据不同的情况，回收不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch){
                //说明当前遍历到的channel不是当前的channel，转发消息
                ch.writeAndFlush(new TextWebSocketFrame("[客户]"+channel.remoteAddress()+"发送了消息："+msg.text()));
            }else {
                //说明当前遍历到的channel是当前的channel，表示是自己，回显自己发送的消息给自己
                ch.writeAndFlush(new TextWebSocketFrame("[自己]发送了消息"+msg.text()));
            }
        });
    }

    /**
     * 当web客户端连接后，触发方法
     * */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        //id表示唯一的值，LongText是唯一的，ShortText不是唯一的
        System.out.println("handlerAdded被调用"+ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其他在线的客户端
        /**
         * 该方法会将channelGroup所有的channel遍历，并发送消息，所以我们不需要自己遍历
         * */
        channelGroup.writeAndFlush(new TextWebSocketFrame("[客户端]"+channel.remoteAddress()+"加入聊天..."));
        channelGroup.add(channel);
    }

    /**
     * 当web客户端断开连接后，触发方法
     * */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        System.out.println("handlerRemoved被调用"+ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(new TextWebSocketFrame("[客户端]"+channel.remoteAddress()+"退出聊天..."));
        //此方法执行后会自动将当前channel从channelGroup中移除
    }

    /**
     * 发生异常触发该方法
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();//关闭通道
    }
}
