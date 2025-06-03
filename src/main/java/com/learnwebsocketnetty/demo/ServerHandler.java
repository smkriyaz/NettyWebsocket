package com.learnwebsocketnetty.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final RawDataHandler handler;

    public ServerHandler(RawDataHandler handler) {
        this.handler = handler;
    }

    private static final Set<Channel> clients = ConcurrentHashMap.newKeySet();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String received = msg.toString();
        Map<String, String> raw = new HashMap<>();
        raw.put("8092", received);
        handler.SendMessagetoChannel(raw);
        System.out.println(received);
    }
}
