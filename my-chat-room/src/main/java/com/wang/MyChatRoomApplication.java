package com.wang;

import com.wang.config.NettyConfig;
import com.wang.server.WebSocketNettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 在平常开发中可能需要实现在启动后执行的功能，
 * Springboot提供了一种简单的实现方案，即实现CommandLineRunner接口，
 * 实现功能的代码在接口的run方法里。
 * */
@SpringBootApplication
public class MyChatRoomApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MyChatRoomApplication.class, args);
    }

    @Autowired
    private WebSocketNettyServer webSocketNettyServer;

    @Autowired
    private NettyConfig nettyConfig;

    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                webSocketNettyServer.start(nettyConfig.getPort());
            }
        }).start();
    }
}
