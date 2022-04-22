package com.wang.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 向文件里写东西
 * */
public class NIOFileChannel01 {
    public static void main(String[] args) throws Exception {
        String str = "hello，my friend";

        //创建一个输出流->channel
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");
        //通过fileOutputStream获取对应的FileChannel
        //这个FileChannel是一个接口，其真实类型是FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();
        //创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将str放入byteBuffer
        byteBuffer.put(str.getBytes());
        //对byteBuffer就行反转，变成可以读
        byteBuffer.flip();
        //将byteBuffer数据写入到fileChannel,此处的byteBuffer是读操作，而fileChannel是写操作
        fileChannel.write(byteBuffer);
        //关闭输出流
        fileOutputStream.close();
    }
}
