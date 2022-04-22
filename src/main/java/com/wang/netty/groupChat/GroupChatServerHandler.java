package com.wang.netty.groupChat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 先定义一个channel组，管理所有的channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     * */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //handlerAdded 表示连接建立，一旦连接，第一个被执行
    /**
     * 将当前的channel加入奥channelGroup
     * */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其他在线的客户端
        /**
         * 该方法会将channelGroup所有的channel遍历，并发送消息，所以我们不需要自己遍历
         * */
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+"加入聊天...("+sdf.format(new Date()) +")\n");
        channelGroup.add(channel);
    }

    /**
     * 断开连接会被触发,将xx客户离开信息推送给当前在线的客户
     * */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+"退出聊天...("+sdf.format(new Date()) +")\n");
        //此方法执行后会自动将当前channel从channelGroup中移除

    }

    /**
     * 表示channel处于一个活动状态，提示xx上线
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"上线了...("+sdf.format(new Date()) +")\n");
    }

    /**
     * 当channel处于非活动状态，提示xx离线了
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"离线了...("+sdf.format(new Date()) +")\n");
    }

    /**
    * 读取数据
    * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取到当前channel
        Channel channel = ctx.channel();
        //这时我们遍历channelGroup，根据不同的情况，回收不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch){
                //说明当前遍历到的channel不是当前的channel，转发消息
                ch.writeAndFlush("[客户]"+channel.remoteAddress()+"发送了消息："+msg+"("+sdf.format(new Date()) +")\n");
            }else {
                //说明当前遍历到的channel是当前的channel，表示是自己，回显自己发送的消息给自己
                ch.writeAndFlush("[自己]发送了消息"+msg+"("+sdf.format(new Date()) +")\n");
            }
        });
    }

    /**
     * 异常处理
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //出现异常，关闭通道
        ctx.close();
    }
}
