package com.wang;

import com.wang.config.NettyConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyChatRoomApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    NettyConfig nettyConfig;

    @Test
    void test1() {
        System.out.println(nettyConfig.getPath());
        System.out.println(nettyConfig.getPort());
    }

}
