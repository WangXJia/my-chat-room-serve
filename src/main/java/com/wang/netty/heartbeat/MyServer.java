package com.wang.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServer {
    public static void main(String[] args) throws Exception {
        //创建两个线程组
        /**
         * 说明：
         * 1.创建两个线程组 bossGroup 和 workerGroup
         * 2.bossGroup只是处理连接请求，真正的和客户端业务处理，会交给workerGroup完成
         * 3.两个都是无限循环
         * */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入netty提供的IdleStateHandler
                            /**
                             * 说明
                             * ①IdleStateHandler 是netty提供的处理空闲状态的处理器
                             * ②long readerIdleTime ：表示多长时间没有读，就会发送一个心跳检测包检测是否还是连接的状态
                             * ③long writeIdleTime ： 表示多长时间没有写，就会发送一个心跳检测包检测是否还是连接的状态
                             * ④long allIdleTime ：   表示多长时间没有读和写，就会发送一个心跳检测包检测是否还是连接的状态
                             * 这里是触发事件IdleStateEvent的handler，进一步处理需要另一个handler
                             *
                             * ⑤当IdleStateEvent触发后，就会传递给管道的下一个handler去处理，通过调用（触发）下一个handler
                             *   的userEventTiggered，在该方法中处理事件，可能是读空闲、写空闲、读写空闲
                             * */
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            //加入一个对空闲检测进一步处理的handler（自定义）
                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            //启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(8081).sync();
            channelFuture.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
