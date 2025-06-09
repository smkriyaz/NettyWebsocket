package com.learnwebsocketnetty.demo;

import io.netty.channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import jakarta.annotation.PostConstruct;

import java.util.List;

@Component
public class NettyServer {


    private DeviceConfigLoader configLoader;

    private ApplicationContext context;

    private final RawDataHandler handler;

    //private static final Map<String , List<EventLoopGroup>>EventsLoop =

    @Autowired
    public NettyServer(RawDataHandler handler,DeviceConfigLoader configLoader,ApplicationContext context) {
        System.out.println("NettyServer up");
        this.handler = handler;
        this.configLoader=configLoader;
        this.context=context;
    }

    //  private int port = 8092;

    public void start(int port, ChannelInboundHandlerAdapter handler) throws InterruptedException {
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(handler);
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });
            ChannelFuture future = b.bind(port).sync();

            System.out.println("Server Started on this port " + port);
            future.channel().closeFuture().sync();

        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

//    @PostConstruct
//    public void startServer() {
//        int [] port  = {8091,8092};
//        new Thread(() -> {
//            try {
//                start();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }).start();
//    }

    @PostConstruct
    public void startServer() {
        List<DeviceConfig> configs = configLoader.getDeviceConfigs();
        for (DeviceConfig config : configs) {
            int port = config.getPort();
            String handlerClassName = config.getHandlerClass();
            try {
                // Dynamically load handler class
                Class<?> clazz = Class.forName(handlerClassName);
                ChannelInboundHandlerAdapter handler = (ChannelInboundHandlerAdapter) context.getBean(clazz);
                // Start server in a thread
                new Thread(() -> {
                    try {
                        start(port, handler);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}