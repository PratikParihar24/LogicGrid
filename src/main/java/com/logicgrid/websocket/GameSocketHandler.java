package com.logicgrid.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GameSocketHandler extends TextWebSocketHandler {

    // 1. When a player successfully connects to the Arena
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WEBSOCKET: New player connected! Session ID: " + session.getId());
    }

    // 2. When a player sends a live message (like submitting code)
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("WEBSOCKET: Message received from " + session.getId() + ": " + message.getPayload());
        
        // Echo a response back to the browser just to prove it works
        session.sendMessage(new TextMessage("Server received your message loud and clear!"));
    }

    // 3. When a player closes the browser or hits "Cancel"
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WEBSOCKET: Player disconnected. Session ID: " + session.getId());
    }
}