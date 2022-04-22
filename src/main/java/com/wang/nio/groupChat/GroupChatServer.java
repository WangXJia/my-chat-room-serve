package com.wang.nio.groupChat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    //定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    //构造器
    public GroupChatServer() {
        try {
            //得到选择器
            Selector open = Selector.open();
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            listenChannel.configureBlocking(false);
            //将listenChannel注册到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {
            //循环处理
            while (true){
                int count = selector.select();
                if (count>0){
                    //说明有事件处理
                    //遍历得到selectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        //取出selectionKey
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()){
                            //监听到accept事件
                            SocketChannel sc = listenChannel.accept();
                            //设置为非阻塞
                            sc.configureBlocking(false);
                            //将sc注册到selector
                            sc.register(selector,SelectionKey.OP_READ);
                            System.out.println(sc.getRemoteAddress()+"已上线...");
                        }
                        if (key.isReadable()){
                            //通道发生read事件，即通道是可读的状态
                            //处理读的逻辑
                            readData(key);
                        }
                        //当前的key删除，防止重复处理
                        iterator.remove();
                    }

                }else {
                    //没有事件处理
                    System.out.println("等待...");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    //读取客户端消息
    private void readData(SelectionKey key){
       //定义一个socketChannel
       SocketChannel channel = null;
       try {
           //得到channel
           channel = (SocketChannel) key.channel();
           //创建buffer
           ByteBuffer buffer = ByteBuffer.allocate(1024);

           int read = channel.read(buffer);
           //根据read的值处理
           if (read>0){
               //说明缓冲区读到数据，将缓存区的数据转成字符串
               String msg = new String(buffer.array());
               //输出该消息
               System.out.println("from 客户端："+ msg);
               //向其他的客户端转发消息,专门写一个方法处理
               sendInfoToOtherClients(msg,channel);
           }
       }catch (IOException e){
           try {
               System.out.println(channel.getRemoteAddress()+"离线了...");
               //取消注册
               key.cancel();
               //关闭通道
               channel.close();
           } catch (IOException ex) {
               ex.printStackTrace();
           }
       }
    }

    //转发消息给其他客户（通道）
    private void sendInfoToOtherClients(String msg,SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中...");
        //遍历到所有注册到selector上的SocketChannel，并排除self
        for (SelectionKey key:selector.keys()){
            //提供key取出对应的SocketChannel
            Channel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self ){
                //开始转发
                //转型
                SocketChannel dest = (SocketChannel) targetChannel;
                //将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入通道
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        //创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
