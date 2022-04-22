package com.wang.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 说明：
 * 1.MapperByteBuffer可让文件直接在内存（堆外内存）修改，操作系统不需要拷贝一次
 * */
public class MapperByteBufferTest {
    public static void main(String[] args) throws Exception{
        RandomAccessFile randomAccessFile = new RandomAccessFile("d:\\file01.txt","rw");

        //获取对应的channel
        FileChannel channel = randomAccessFile.getChannel();

        /**
         * 参数1：FileChannel.MapMode.READ_WRITE 使用读写模式
         * 参数2：可以直接修改的起始位置
         * 参数3：映射到内存的大小，即将文件的多少个字节有多少个，也说明直接修改的范围是0-5，5不是下标位置，是大小
         *
         * 实际类型是DirectByteBuffer
         * */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0,(byte)'H');
        mappedByteBuffer.put(3,(byte)'9');

        randomAccessFile.close();


    }
}
