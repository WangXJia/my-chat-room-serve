package com.wang.netty.webSocket;

import com.wang.netty.heartbeat.MyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
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
                            /*因为基于http协议，使用http的编码和解码器* */
                            pipeline.addLast(new HttpServerCodec());
                            //是以块方式写，添加ChunkedWrite处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            /*
                              说明
                              ①http数据在传输过程中是分段，HttpObjectAggregator就是可以将多个段聚合起来
                              ②这就是为什么当浏览器发生大量数据时，就会发出多次http请求
                              */
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /*
                            * 说明：
                            * ①对应webSocket，它的数据是以 帧（frame） 形式传递
                            * ②可以看到WebSocketFrame下面有六个子类
                            * ③浏览器发送请求时 ws://localhost:7000/xxx xxx表示请求的uri
                            * ④WebSocketServerProtocolHandler 的核心功能是将http协议升级为ws协议，保持长连接
                            * */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            /*
                            * 自定义的handler，处理业务逻辑
                            * */
                            pipeline.addLast(new MyTextWebSocketFrameHandler());
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
