package com.learnwebsocketnetty.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;

@Component
public class RawDataHandler extends TextWebSocketHandler {

    private Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
   // private Map<WebSocketSession,String> clientSessions = new HashMap<>();
   private final Map<Integer, Set<WebSocketSession>> clientSessions = new ConcurrentHashMap<>();



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println("New WebSocket connection established: " + session.getId());
        System.out.println("New WebSocket connection established: " + session.getUri());

        sessions.add(session);
        System.out.println("New Swebsocket connection added"+session);
        session.sendMessage(new TextMessage("Hello you are connected"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("WebSocket connection closed: " + session.getId() + " with status: " + status);
        sessions.remove(session);
        int port= session.getUri().getPort();
        Set<WebSocketSession> toRemoveSession= clientSessions.get(port);
        if(toRemoveSession!=null){
            toRemoveSession.remove(session);
            System.out.println("Websocket connection disconnected"+ session);
        }


    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        JSONObject obj = new JSONObject(payload);
       int port= obj.getInt("port");
       System.out.println(port);
        System.out.println(payload);
        Set<WebSocketSession> sessions = clientSessions.computeIfAbsent(
                port,
                k -> ConcurrentHashMap.newKeySet()
        );
        sessions.add(session);
//        Set<WebSocketSession> sessions=  clientSessions.get(port);
//        if(sessions!=null){
//            sessions.add(session);
//        }else{
//           Set<WebSocketSession> clientSessions1 = ConcurrentHashMap.newKeySet();
//            clientSessions1.add(session);
//            clientSessions.put(port,clientSessions1);
//        }

    }

    @PostConstruct
    public void websocketMessage() {
        System.out.println("Webconfig Handler Started");
    }

    public void SendMessagetoChannel(Map<Integer, String> raw, int port) throws IOException {
        Set<WebSocketSession>clientsendMessage= clientSessions.get(port);
        if(clientsendMessage!=null){
            for(WebSocketSession see:clientsendMessage){
                if(see.isOpen()){
                 String message=  raw.get(port);
                 see.sendMessage(new TextMessage(message));
                }
            }
        }
        System.out.println("Session not found");

    }
}