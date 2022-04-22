package com.wang.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 向文件里拿东西
 * */
public class NIOFileChannel02 {
    public static void main(String[] args) throws Exception {
        //创建文件输入流
        File file = new File("d:\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        //通过输入流对象获取相应的FileChannel
        FileChannel fileChannel = fileInputStream.getChannel();
        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        //将通道的数据读入到Buffer中
        fileChannel.read(byteBuffer);
        //将byteChannel的字节数据转成String
        System.out.println(new String(byteBuffer.array()));
        //关闭输入流
        fileInputStream.close();
    }
}
