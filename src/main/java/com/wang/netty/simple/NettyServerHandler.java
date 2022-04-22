package com.wang.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 说明
 * 1.自定义一个Handler需要继承netty规定好的某个HandlerAdapter，因为里面有很多规范需要遵守
 * 2.这是我们自定义的一个handler，才称为一个handler
 * */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**读取数据事件（这里我们可以读取客户端发送的消息）
     * 1.ChannelHandlerContext ctx :上下文对象,含有 管道pipeline、通道、地址
     * 2.Object msg：就是客户端发送的数据 默认为Object
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //比如这里有一个非常耗费时间的业务->异步执行->提交该channel对应的NioEventLoop到taskQueue中

        /**
         * 解决方案1 用户程序自定义的普通任务
         * */
//        ctx.channel().eventLoop().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10*1000);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~，这个操作耗时很长",CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
        /**
         * 用户自定义定时任务->该任务是提交到scheduleTestQueue中
         * */
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~，这个操作耗时很长",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },5, TimeUnit.SECONDS);
        System.out.println("go on...");

//        System.out.println("服务器读取线程："+Thread.currentThread().getName());
//        System.out.println("server ctx = "+ctx);
//        System.out.println("看看channel和pipeline的关系");
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline(); //本质是一个双向链表
//        //将msg转成一个ByteBuf
//        //ByteBuf是netty提供的，不是Nio的ByteBuffer
//        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("客户端发送的消息是："+buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址："+ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕后的操作
     * */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //writeAndFlush 是 write + flush 两个方法的合并
        //将数据写入到缓存并刷新
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~",CharsetUtil.UTF_8));
    }

    /**
     * 处理异常,一般需要关闭通道
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
