package com.learnwebsocketnetty.demo;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;

@Component
public class RawDataHandler extends TextWebSocketHandler {

    private Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println("New WebSocket connection established: " + session.getId());
        System.out.println("New WebSocket connection established: " + session.getUri());

        sessions.add(session);
        session.sendMessage(new TextMessage("Hello you are connected"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("WebSocket connection closed: " + session.getId() + " with status: " + status);
        sessions.remove(session);
    }

    @PostConstruct
    public void websocketMessage() {
        System.out.println("Webconfig Handler Started");
    }

    public void SendMessagetoChannel(Map<String, String> raw) throws IOException {
        String rawData = raw.get("8092");
        for (WebSocketSession sess : sessions) {
            if (sess.isOpen()) {
                sess.sendMessage(new TextMessage(rawData));
            }
        }
    }
}