package com.logicgrid.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.logicgrid.models.Question;
import com.logicgrid.services.QuestionVault;

public class GameSocketHandler extends TextWebSocketHandler {

    // 1. The Waiting Room (Thread-safe so two players connecting at the exact same millisecond don't crash the server)
    private static Queue<WebSocketSession> matchmakingQueue = new ConcurrentLinkedQueue<>();
    private Gson gson = new Gson();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WEBSOCKET: Player joined queue. Session ID: " + session.getId());
        matchmakingQueue.add(session);
        
        // Every time someone joins, check if we have enough players for a match
        checkForMatch(); 
    }

    // 2. The Matchmaker Logic
    private synchronized void checkForMatch() throws Exception {
        if (matchmakingQueue.size() >= 2) {
            
            // Pull the first two players out of the queue
            WebSocketSession player1 = matchmakingQueue.poll();
            WebSocketSession player2 = matchmakingQueue.poll();

            // Grab a 3-question Gauntlet from the Problem Vault
            List<Question> gauntlet = QuestionVault.getGauntlet();

            // Build the JSON Payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "MATCH_FOUND");
            payload.put("questions", gauntlet);

            // Convert the Java Map into a JSON String
            String jsonMessage = gson.toJson(payload);
            TextMessage message = new TextMessage(jsonMessage);

            // Fire the questions to both players simultaneously!
            if (player1.isOpen()) player1.sendMessage(message);
            if (player2.isOpen()) player2.sendMessage(message);

            System.out.println("MATCHMAKER: Match started! Fired questions to " + player1.getId() + " and " + player2.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // We will put the game scoring logic here in the next step
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // If they close the browser while searching, remove them from the line
        matchmakingQueue.remove(session);
        System.out.println("WEBSOCKET: Player left queue. Session ID: " + session.getId());
    }
}