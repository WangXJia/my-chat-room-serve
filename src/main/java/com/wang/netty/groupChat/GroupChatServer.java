package com.wang.netty.groupChat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class GroupChatServer {

    private int port; //监听端口

    public GroupChatServer(int port){
        this.port = port;
    }

    //编写run方法，处理客户端的请求
    public void run() throws Exception{
        //创建两个线程组
        /**
         * 说明：
         * 1.创建两个线程组 bossGroup 和 workerGroup
         * 2.bossGroup只是处理连接请求，真正的和客户端业务处理，会交给workerGroup完成
         * 3.两个都是无限循环
         * */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加处理器，向pipeline加入一个解码器，是netty自带的
                            pipeline.addLast("decoder",new StringDecoder());
                            //向pipeline加入一个编码器，是netty自带的
                            pipeline.addLast("encoder",new StringEncoder());
                            //向pipeline加入自己的业务处理handler
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });
            System.out.println("netty 服务器已经启动...");
            ChannelFuture channelFuture = b.bind(port).sync();

            //监听关闭事件
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception{
        new GroupChatServer(8081).run();
    }
}
