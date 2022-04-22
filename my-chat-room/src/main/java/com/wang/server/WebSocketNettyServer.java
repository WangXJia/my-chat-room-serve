package com.wang.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**实现DisposableBean 在容器销毁前会调用destroy 方法进行线程组的关闭*/
@Component
public class WebSocketNettyServer implements DisposableBean {

    /**
     * boos线程组
     */
    private EventLoopGroup boos;

    /**
     * work线程组
     */
    private EventLoopGroup work;


    /**
     * 初始化器
     * */
    @Autowired
    WebSocketChannelInit webSocketChannelInit;

    /**
     * 自定义启动方法
     * @param port
     */
    public void start(int port) {
        // 设置boos线程组
        boos = new NioEventLoopGroup(1);
        // 设置work线程组
        work = new NioEventLoopGroup();
        // 创建启动助手
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boos,work)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler())
                .childHandler(webSocketChannelInit);
        // 绑定ip和端口启动服务端
        ChannelFuture sync = null;
        try {
            // 绑定netty的启动端口
            sync = serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            close();
        }
        System.out.println("netty服务器启动成功"+"--端口:"+port);
        sync.channel().closeFuture();
    }


    //该bean被销毁就会执行
    @Override
    public void destroy() throws Exception {
        close();
    }

    public void close() {
        if (boos!=null) {
            boos.shutdownGracefully();
        }
        if (work!=null) {
            work.shutdownGracefully();
        }
    }
}
