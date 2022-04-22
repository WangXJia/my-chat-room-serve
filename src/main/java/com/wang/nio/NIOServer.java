package com.wang.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception{

        //1.创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2.得到一个Selector对象
        Selector selector = Selector.open();
        //3.绑定一个端口6666，在服务端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //4.设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //5.把serverSocketChannel注册到selector ，关心事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6.循环等待客户端连接
        while (true){
            //6-1.这里等待一秒钟（1000），没有事件发生，返回
            if (selector.select(1000) == 0){
                System.out.println("服务端等待一秒，无连接");
                continue;
            }
            //6-2.如果返回的大于0，就获取到相关的selectionKey集合。如果返回大于0，表示已经获取到关注的事件；
            //6-3.selector.selectedKeys()返回关注事件的集合,通过selectionKeys可以反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //6-4.遍历Set<SelectionKey>，使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()){
                //6-5.获取到selectionKey
                SelectionKey key = keyIterator.next();
                //6-6.根据key对应的通道发生的事件做相应的处理,如果是OP_ACCEPT，表示有新的客户端连接
                if (key.isAcceptable()){
                    //6-7.为该客户端生成一个SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功，生成一个socketChannel + "+socketChannel.hashCode());
                    //6-8.将socketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //6-9.将socketChannel注册到selector，关注事件为OP_READ，同时给socketChannel关联一个Buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if (key.isReadable()){
                    //6-10.如果是OP_READ，通过key反向获得channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //6-11.获取该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println("from 客户端 "+new String(buffer.array()));
                }
                //手动从map中移除当前的selectionKey，防止重复操作
                keyIterator.remove();
            }
        }









    }
}
