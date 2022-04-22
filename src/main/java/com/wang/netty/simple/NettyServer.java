package com.wang.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 分析
 * 1.bossGroup 和 workerGroup 是怎么确定下面含有多少个子线程，即NioEventLoopGroup
 *   ①默认是 cpu核数*2，workerGroup使用子线程用的是轮询机制
 * 2.ctx到底包含什么，channel 和 pipeline 和 ctx 的关系
 *   ①channel 和 pipeline 是相互包含的关系
 *   ②ctx包含了channel 和 pipeline，还包含了其他更多的信息
 *
 * */
public class NettyServer {
    public static void main(String[] args) throws Exception {
        //创建BossGroup 和 WorkerGroup
        /**
         * 说明：
         * 1.创建两个线程组 bossGroup 和 workerGroup
         * 2.bossGroup只是处理连接请求，真正的和客户端业务处理，会交给workerGroup完成
         * 3.两个都是无限循环
         * */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来进行设置
            bootstrap.group(bossGroup,workerGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用NioSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128) //设置线程队列等待连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道测试对象（匿名对象）
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //可以使用一个集合管理socketChannel，在推送消息时，可以将业务加入到各个channel对应的nioEventLoop的taskQueue或scheduleTaskQueue
                            System.out.println("客户对应的socketChannel hashCode："+ch.hashCode());
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //给我们的workerGroup的EventLoop对应的管道设置处理器
            System.out.println("....服务器已经准备好.....");

            //绑定端口,并且同步，生成一个ChannelFuture对象,此处相当于启动服务器
            ChannelFuture cf = bootstrap.bind(6669).sync();

            //给cf注册监听器，监控我们关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                   if (cf.isSuccess()){
                       System.out.println("监听6669端口成功！！");
                   }else {
                       System.out.println("监听6669端口失败！！");
                   }
                }
            });

            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
