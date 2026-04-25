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
            
            // Check if they clicked Surrender
            if ("SURRENDER".equals(payload.get("type"))) {
                handleForfeit(session);
                return; // Stop processing, the game is over
            }
            
            if ("SUBMIT_ANSWER".equals(payload.get("type"))) {
                String userAnswer = payload.get("answer").trim();
                Question currentQuestion = currentMatch.getCurrentQuestion();
                
                if (userAnswer.equalsIgnoreCase(currentQuestion.getCorrectAnswer().trim())) {
                    System.out.println("REFEREE: " + session.getId() + " got it RIGHT!");
                    currentMatch.addPoint(session);
                    
                    // --- NEW: SPRINT 3 STEP 3 (OPPONENT SYNC) ---
                    // 1. Identify the opponent
                    WebSocketSession opponent = (currentMatch.getPlayer1().getId().equals(session.getId())) 
                                                ? currentMatch.getPlayer2() 
                                                : currentMatch.getPlayer1();
                                                
                    // 2. Fire the warning if they are still connected
                    if (opponent.isOpen()) {
                        com.logicgrid.models.User scorer = (com.logicgrid.models.User) session.getAttributes().get("loggedInUser");
                        Map<String, String> syncMsg = new HashMap<>();
                        syncMsg.put("type", "OPPONENT_SCORED");
                        syncMsg.put("message", scorer.getUsername() + " solved the question!");
                        opponent.sendMessage(new TextMessage(gson.toJson(syncMsg)));
                    }
                    // --------------------------------------------

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
        String p1Result = "";
        String p2Result = "";

        // 🛡️ CAPTURE OLD ELO FIRST (So we can calculate the exact +/- change)
        int oldP1Elo = p1.getEloRating();
        int oldP2Elo = p2.getEloRating();

        // 2. Figure out who won and calculate Elo!
        if (match.getPlayer1Score() > match.getPlayer2Score()) {
            resultMessage = p1.getUsername() + " Wins! (" + match.getPlayer1Score() + "-" + match.getPlayer2Score() + ")";
            EloCalculator.updateRatings(p1, p2); // p1 is winner
            p1Result = "Win";
            p2Result = "Loss";
            
        } else if (match.getPlayer2Score() > match.getPlayer1Score()) {
            resultMessage = p2.getUsername() + " Wins! (" + match.getPlayer2Score() + "-" + match.getPlayer1Score() + ")";
            EloCalculator.updateRatings(p2, p1); // p2 is winner
            p1Result = "Loss";
            p2Result = "Win";
            
        } else {
            resultMessage = "It's a Tie! (" + match.getPlayer1Score() + "-" + match.getPlayer2Score() + ")";
            // We do nothing to Elo on a tie to keep it simple for now
            p1Result = "Tie";
            p2Result = "Tie";
        }

        // Calculate the exact Elo change (e.g., +16 or -14)
        int p1EloChange = p1.getEloRating() - oldP1Elo;
        int p2EloChange = p2.getEloRating() - oldP2Elo;

        // 3. Save the new ratings directly to the MySQL Database
        userDao.updateUser(p1);
        userDao.updateUser(p2);

        // 🚀 4. GENERATE AND SAVE MATCH HISTORY TICKETS
        com.logicgrid.models.MatchRecord record1 = new com.logicgrid.models.MatchRecord(p1, p2.getUsername(), p1Result, p1EloChange);
        com.logicgrid.models.MatchRecord record2 = new com.logicgrid.models.MatchRecord(p2, p1.getUsername(), p2Result, p2EloChange);
        
        userDao.saveMatchRecord(record1);
        userDao.saveMatchRecord(record2);

        // 5. Create custom Game Over envelopes to show their exact new Elo
        Map<String, String> p1Payload = new HashMap<>();
        p1Payload.put("type", "GAME_OVER");
        p1Payload.put("message", resultMessage + " | Your New Elo: " + p1.getEloRating());
        
        Map<String, String> p2Payload = new HashMap<>();
        p2Payload.put("type", "GAME_OVER");
        p2Payload.put("message", resultMessage + " | Your New Elo: " + p2.getEloRating());

        // 6. Fire the final messages
        if (match.getPlayer1().isOpen()) match.getPlayer1().sendMessage(new TextMessage(gson.toJson(p1Payload)));
        if (match.getPlayer2().isOpen()) match.getPlayer2().sendMessage(new TextMessage(gson.toJson(p2Payload)));
        
        // 7. Clear the arena
        activeMatches.remove(match.getPlayer1());
        activeMatches.remove(match.getPlayer2());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        matchmakingQueue.remove(session);
        // If they disconnect mid-match, we handle the forfeit here later
        activeMatches.remove(session); 
        
        // NEW: If they close the browser while actively in a match, penalize them!
        if (activeMatches.containsKey(session)) {
            handleForfeit(session);
        }
        
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
 // --- THE FORFEIT ENGINE ---
    private void handleForfeit(WebSocketSession forfeitingSession) throws Exception {
        Match currentMatch = activeMatches.get(forfeitingSession);
        if (currentMatch == null) return; // If they aren't in a match, do nothing

        // 1. Identify the Winner and the Loser
        WebSocketSession winnerSession = (currentMatch.getPlayer1().equals(forfeitingSession)) 
                                         ? currentMatch.getPlayer2() 
                                         : currentMatch.getPlayer1();

        com.logicgrid.models.User loser = (com.logicgrid.models.User) forfeitingSession.getAttributes().get("loggedInUser");
        com.logicgrid.models.User winner = (com.logicgrid.models.User) winnerSession.getAttributes().get("loggedInUser");

        System.out.println("REFEREE: " + loser.getUsername() + " fled the battle! " + winner.getUsername() + " wins by default.");

        // 2. The Penalty Math (Winner beat Loser)
        EloCalculator.updateRatings(winner, loser);
        userDao.updateUser(winner);
        userDao.updateUser(loser);

        // 3. Rescue the Survivor
        if (winnerSession.isOpen()) {
            Map<String, String> winPayload = new HashMap<>();
            winPayload.put("type", "GAME_OVER");
            winPayload.put("message", "Opponent Fled! You Win! | Your New Elo: " + winner.getEloRating());
            winnerSession.sendMessage(new TextMessage(gson.toJson(winPayload)));
        }

        // 4. Acknowledge the Surrender (Only works if they clicked the button and didn't close the tab)
        if (forfeitingSession.isOpen()) {
            Map<String, String> losePayload = new HashMap<>();
            losePayload.put("type", "GAME_OVER");
            losePayload.put("message", "You Surrendered. | Your New Elo: " + loser.getEloRating());
            forfeitingSession.sendMessage(new TextMessage(gson.toJson(losePayload)));
        }

        // 5. Clear the Arena
        activeMatches.remove(currentMatch.getPlayer1());
        activeMatches.remove(currentMatch.getPlayer2());
    }
}