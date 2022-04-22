package com.wang.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public class NettyByteBuf02 {
    public static void main(String[] args) {
        //创建ByteBuf：第二种方式
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!", Charset.forName("utf-8"));
        //使用相关的api
        if (byteBuf.hasArray()){
            byte[] content = byteBuf.array();

            //将content转成字符串
            System.out.println(new String(content,Charset.forName("utf-8")));

            System.out.println(byteBuf.arrayOffset());
            System.out.println(byteBuf.readerIndex());
            System.out.println(byteBuf.writerIndex());
            System.out.println(byteBuf.capacity());

            int len = byteBuf.readableBytes(); //可读的字节数
            System.out.println(len);

            //使用for取出各个循环
            for (int i = 0; i < len; i++) {
                System.out.println((char) byteBuf.getByte(i));
            }

            //读取某一段
            System.out.println(byteBuf.getCharSequence(0, 4, Charset.forName("utf-8")));
        }
    }
}
