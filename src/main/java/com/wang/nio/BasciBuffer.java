package com.wang.nio;

import java.nio.IntBuffer;

public class BasciBuffer {
    public static void main(String[] args) {
        //举例说明Buffer的使用（简单说明）
        //创建一个buffer，大小为5，即可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向buffer存放数据
        for(int i=0;i<intBuffer.capacity();i++){
            intBuffer.put(i*2);
        }

        //从buffer读取数据
        //将buffer转换，读写转换
        intBuffer.flip();
        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
