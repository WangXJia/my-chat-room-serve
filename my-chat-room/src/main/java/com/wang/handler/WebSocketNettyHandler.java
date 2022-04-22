package com.wang.handler;

import com.alibaba.fastjson.JSON;
import com.wang.entity.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebSocketNettyHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    /**
     * 存储用户对应的通道
     */
    public static Map<String,ChannelHandlerContext> MAP = new HashMap<>();

    /**
     * 存放通道到用户关联
     */
    public static Map<String,String> CHANNEL_USER = new HashMap<>();

    /**
     * 存储当前连接上的通道
     */
    public static List<ChannelHandlerContext> LIST = new ArrayList<>();

    /**
     * 通道连接事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LIST.add(ctx);
        System.out.println("新的连接启动，当前连接数量:"+LIST.size());
    }

    /**
     * 通道消息事件
     * @param channelHandlerContext
     * @param textWebSocketFrame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        System.out.println("前端发来的消息:"+textWebSocketFrame.text());
        Message message = JSON.parseObject(textWebSocketFrame.text(), Message.class);
        System.out.println(message.toString());
        if (message.getType()==1) {
            setMap(channelHandlerContext,message);
            // 给其他服务器发送上线消息
            for (ChannelHandlerContext handlerContext : MAP.values()) {
                if (handlerContext==channelHandlerContext) {
                    continue;
                }
                handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
            }
            return;
        }
        // 获取到需要转发的客户端
        String receive = message.getReceive();
        // 没有指定接收者代表要群发
        if (StringUtil.isNullOrEmpty(receive)) {
            for (ChannelHandlerContext handlerContext : MAP.values()) {
                if (handlerContext==channelHandlerContext) {
                    continue;
                }
                handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
            }
            return;
        }
        // 从缓存的存储用户对应的通道 map中获取
        if (!MAP.containsKey(receive)) {
            Message message1 = new Message("服务端",channelHandlerContext.name(), UUID.randomUUID().toString(),"用户未在线，你的消息不能及时送达。",2);
            channelHandlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message1)));
            return;
        }
        // 服务端转发消息到指定的客户端
        ChannelHandlerContext channelHandlerContext1 = MAP.get(receive);
        channelHandlerContext1.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    }

    /**
     * 设置连接映射
     * @param channelHandlerContext
     * @param message
     */
    private void setMap(ChannelHandlerContext channelHandlerContext, Message message) {
        MAP.put(message.getId(),channelHandlerContext);
        CHANNEL_USER.put(channelHandlerContext.channel().id().toString(),message.getSend());
    }

    /**
     * 通达关闭事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String s = CHANNEL_USER.get(ctx.channel().id().toString());
        MAP.remove(s);
        // 给其他在线用户发送该用户离线的信息
        for (ChannelHandlerContext handlerContext : MAP.values()) {
            Message message = new Message("服务端",null, UUID.randomUUID().toString(),"用户--"+s+"--已经离线了",2);
            handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
        }
        LIST.remove(ctx);
        CHANNEL_USER.remove(ctx.channel().id().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String s = CHANNEL_USER.get(ctx.channel().id().toString());
        MAP.remove(s);
        // 给其他在线用户发送该用户离线的信息
        for (ChannelHandlerContext handlerContext : MAP.values()) {
            Message message = new Message("服务端",null, UUID.randomUUID().toString(),"用户--"+s+"--连接发生问题，已被迫离线了",2);
            handlerContext.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
        }
        LIST.remove(ctx);
        CHANNEL_USER.remove(ctx.channel().id().toString());
    }
}
