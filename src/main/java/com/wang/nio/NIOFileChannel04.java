package com.wang.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * 使用transferFrom完成图片的拷贝
 * */
public class NIOFileChannel04 {
    public static void main(String[] args) throws Exception {
        //创建相关流
        FileInputStream fileInputStream = new FileInputStream("d:\\a.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\a2.jpg");
        //获取各个流对应的fileChannel
        FileChannel sourceChannel = fileInputStream.getChannel();
        FileChannel destChannel = fileOutputStream.getChannel();
        //使用transferForm完成拷贝
        destChannel.transferFrom(sourceChannel,0,sourceChannel.size());

        //关闭
        sourceChannel.close();
        destChannel.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
