package com.logicgrid.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.logicgrid.models.Match;
import com.logicgrid.models.Question;
import com.logicgrid.services.EloCalculator;
import com.logicgrid.services.QuestionVault;


public class GameSocketHandler extends TextWebSocketHandler {

    private static Queue<WebSocketSession> matchmakingQueue = new ConcurrentLinkedQueue<>();
    
    // NEW: A Map to track which player belongs to which Match
    private static Map<WebSocketSession, Match> activeMatches = new ConcurrentHashMap<>();
    
    
    private Gson gson = new Gson();
    private com.logicgrid.dao.UserDao userDao = new com.logicgrid.dao.UserDao();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        
        // 1. The Identity Bridge: Open the luggage and grab the User object
        Map<String, Object> attributes = session.getAttributes();
        com.logicgrid.models.User player = (com.logicgrid.models.User) attributes.get("loggedInUser");
        
        // 2. Print their actual identity to the console
        if (player != null) {
            System.out.println("WEBSOCKET: " + player.getUsername() + " (Elo: " + player.getEloRating() + ") joined the Arena!");
        } else {
            System.out.println("WEBSOCKET: Ghost player connected (No session found).");
        }
        
        // 3. Put them in the waiting room
        matchmakingQueue.add(session);
        
        // 4. The "Too Fast" Bug Fix: Spin up a tiny background thread to wait 0.1 seconds
        new Thread(() -> {
            try {
                Thread.sleep(100); // Give Tomcat time to finish opening the connection
                checkForMatch();   // Now check the queue!
            } catch (Exception e) {
                System.err.println("Error in matchmaker thread: " + e.getMessage());
            }
        }).start();
    }

    private synchronized void checkForMatch() throws Exception {
        if (matchmakingQueue.size() >= 2) {
            WebSocketSession player1 = matchmakingQueue.poll();
            WebSocketSession player2 = matchmakingQueue.poll();
            List<Question> gauntlet = QuestionVault.getGauntlet();

            // NEW: Create the Match object and save it in the active list
            Match newMatch = new Match(player1, player2, gauntlet);
            activeMatches.put(player1, newMatch);
            activeMatches.put(player2, newMatch);

            // Tell the browser the match started
            broadcastQuestion(newMatch);
            System.out.println("MATCHMAKER: Match started between " + player1.getId() + " and " + player2.getId());
        }
    }

    // --- THE REFEREE LOGIC ---
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Match currentMatch = activeMatches.get(session);
        if (currentMatch == null) return; 

        try {
            // Attempt to parse the incoming message as JSON
            Map<String, String> payload = gson.fromJson(message.getPayload(), Map.class);
            
            if ("SUBMIT_ANSWER".equals(payload.get("type"))) {
                String userAnswer = payload.get("answer").trim();
                Question currentQuestion = currentMatch.getCurrentQuestion();
                
                if (userAnswer.equalsIgnoreCase(currentQuestion.getCorrectAnswer().trim())) {
                    System.out.println("REFEREE: " + session.getId() + " got it RIGHT!");
                    currentMatch.addPoint(session);
                    currentMatch.advanceQuestion();
                    
                    if (currentMatch.isGameOver()) {
                        endMatch(currentMatch);
                    } else {
                        broadcastQuestion(currentMatch); 
                    }
                    
                } else {
                    System.out.println("REFEREE: " + session.getId() + " got it WRONG.");
                    Map<String, String> wrongMsg = new HashMap<>();
                    wrongMsg.put("type", "WRONG_ANSWER");
                    wrongMsg.put("message", "Incorrect! Try again.");
                    session.sendMessage(new TextMessage(gson.toJson(wrongMsg)));
                }
            }
        } catch (Exception e) {
            // SHIELD: If the browser sends garbage text, catch the error and ignore it instead of crashing!
            System.err.println("REFEREE WARNING: Received malformed data from " + session.getId() + ". Ignoring.");
        }
    }

    // --- HELPER METHODS ---
    private void broadcastQuestion(Match match) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "MATCH_FOUND"); // Reusing this to update the UI
        payload.put("questions", List.of(match.getCurrentQuestion())); // Just send the current one

        String jsonMessage = gson.toJson(payload);
        TextMessage msg = new TextMessage(jsonMessage);

     // FIX: Create a brand new TextMessage envelope for EACH player!
        if (match.getPlayer1().isOpen()) {
            match.getPlayer1().sendMessage(new TextMessage(jsonMessage));
        }
        if (match.getPlayer2().isOpen()) {
            match.getPlayer2().sendMessage(new TextMessage(jsonMessage));
        }
    }

private void endMatch(Match match) throws Exception {
        
        // 1. Extract the actual User objects from their session luggage
        com.logicgrid.models.User p1 = (com.logicgrid.models.User) match.getPlayer1().getAttributes().get("loggedInUser");
        com.logicgrid.models.User p2 = (com.logicgrid.models.User) match.getPlayer2().getAttributes().get("loggedInUser");
        
        String resultMessage = "";

        // 2. Figure out who won and calculate Elo!
        if (match.getPlayer1Score() > match.getPlayer2Score()) {
            resultMessage = p1.getUsername() + " Wins! (" + match.getPlayer1Score() + "-" + match.getPlayer2Score() + ")";
            EloCalculator.updateRatings(p1, p2); // p1 is winner
            
        } else if (match.getPlayer2Score() > match.getPlayer1Score()) {
            resultMessage = p2.getUsername() + " Wins! (" + match.getPlayer2Score() + "-" + match.getPlayer1Score() + ")";
            EloCalculator.updateRatings(p2, p1); // p2 is winner
            
        } else {
            resultMessage = "It's a Tie! (" + match.getPlayer1Score() + "-" + match.getPlayer2Score() + ")";
            // We do nothing to Elo on a tie to keep it simple for now
        }

        // 3. Save the new ratings directly to the MySQL Database
        userDao.updateUser(p1);
        userDao.updateUser(p2);

        // 4. Create custom Game Over envelopes to show their exact new Elo
        Map<String, String> p1Payload = new HashMap<>();
        p1Payload.put("type", "GAME_OVER");
        p1Payload.put("message", resultMessage + " | Your New Elo: " + p1.getEloRating());
        
        Map<String, String> p2Payload = new HashMap<>();
        p2Payload.put("type", "GAME_OVER");
        p2Payload.put("message", resultMessage + " | Your New Elo: " + p2.getEloRating());

        // 5. Fire the final messages
        if (match.getPlayer1().isOpen()) match.getPlayer1().sendMessage(new TextMessage(gson.toJson(p1Payload)));
        if (match.getPlayer2().isOpen()) match.getPlayer2().sendMessage(new TextMessage(gson.toJson(p2Payload)));
        
        // 6. Clear the arena
        activeMatches.remove(match.getPlayer1());
        activeMatches.remove(match.getPlayer2());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        matchmakingQueue.remove(session);
        // If they disconnect mid-match, we handle the forfeit here later
        activeMatches.remove(session); 
        
     // FIX: The "Too Fast" Bug
        // We spin off a tiny background thread so this method can finish and Tomcat can open the pipe.
        new Thread(() -> {
            try {
                Thread.sleep(100); // Wait 1/10th of a second for Tomcat to finish wiring
                checkForMatch();   // Now check for a match!
            } catch (Exception e) {
                System.err.println("Error in matchmaker thread: " + e.getMessage());
            }
        }).start();
    }
}