package com.learnwebsocketnetty.demo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
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

    private static final Map<Integer, Set<Channel>> clientMap = new ConcurrentHashMap<>();

    public ServerHandler(RawDataHandler handler) {
        this.handler = handler;
    }

    private static final Set<Channel> clients = ConcurrentHashMap.newKeySet();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().localAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        int port = inetSocketAddress.getPort();
//      Set<Channel>currentChanels= clientMap.get(port);
//      if(!currentChanels.contains(ctx.channel())){
//          currentChanels.add(ctx.channel());
//      }
//        clientMap.put(port,currentChanels);
        Set<Channel> currentChannels = clientMap.computeIfAbsent(port, k -> ConcurrentHashMap.newKeySet());
        currentChannels.add(ctx.channel());
        System.out.println("Client connected on port " + port + ": " + ctx.channel() + "and added");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().localAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        int port = inetSocketAddress.getPort();
        Set<Channel> currentChannels = clientMap.get(port);
        if (currentChannels != null) {
            currentChannels.remove(ctx.channel());
            if (currentChannels.isEmpty()) {
                clientMap.remove(port);
            }
        }
        System.out.println("Client disconnected from port " + port + ": " + ctx.channel());

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String received = msg.toString();
        SocketAddress socketAddress = ctx.channel().localAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        int port = inetSocketAddress.getPort();
        Map<Integer, String> raw = new HashMap<>();
        raw.put(port, received);
        handler.SendMessagetoChannel(raw,port);
        System.out.println(received);
    }
}
