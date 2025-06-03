package com.learnwebsocketnetty.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSocket
public class WebConfig implements WebSocketConfigurer {

    private final RawDataHandler handler;

    public WebConfig(RawDataHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/getRawData");
    }

    @PostConstruct
    public void websocketMessage() {
        System.out.println("Webconfig Started");
    }
}