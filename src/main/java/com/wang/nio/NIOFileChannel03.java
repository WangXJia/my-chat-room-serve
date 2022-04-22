package com.wang.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**拷贝一个文件的内容到另一个文件，只用一个buffer*/
public class NIOFileChannel03 {
    public static void main(String[] args) throws Exception {
        File file = new File("d:\\file01.txt");

        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file02.txt");
        FileChannel fileChannel1 = fileOutputStream.getChannel();


        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        while (true){
            //这里有一个重要的操作，一定不要忘了
            byteBuffer.clear(); //清空buffer
            int read = fileChannel.read(byteBuffer);
            if (read == -1){
                break;
            }
            //将buffer中的数据写入到fileChannel1---第二个文件中
            byteBuffer.flip();
            fileChannel1.write(byteBuffer);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }
}
