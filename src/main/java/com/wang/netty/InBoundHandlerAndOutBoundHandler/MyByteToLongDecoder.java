package com.wang.netty.InBoundHandlerAndOutBoundHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyByteToLongDecoder extends ByteToMessageDecoder {

    /**
     * decode会根据接受的数据，被调用多次，直到确定没有新的元素被添加到list，或者ByteBuf没有更多的可读字节为止
     * 如果list out 不为空，就会将list的内容传递给下一个channelInBoundHandler处理
     * channelInBoundHandler处理器的方法也会被运行多次
     *
     * @param ctx 上下文对象
     * @param in 入站的ByteBuf
     * @param out list集合，将解码后的数据传给下一个handler
     * */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyByteToLongDecoder被调用");
        //Long为8字节,需要判断有8个字节才能读取一个Long
        if (in.readableBytes() >= 8){
            out.add(in.readLong());
        }
    }
}
