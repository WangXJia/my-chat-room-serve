package com.wang.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    //发送者
    private String send;

    //接收者
    private String receive;

    //消息id(当前时间戳)
    private String id;

    //消息值
    private String info;


    //消息类型 1->客户端发送的消息，2->服务端发送的消息
    private int type;
}
