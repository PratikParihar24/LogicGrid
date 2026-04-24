package com.logicgrid.models;

import java.util.List;
import org.springframework.web.socket.WebSocketSession;

public class Match {
    
    private WebSocketSession player1;
    private WebSocketSession player2;
    private List<Question> gauntlet;
    private int currentQuestionIndex;
    
    // We will just track who won the most rounds
    private int player1Score;
    private int player2Score;

    public Match(WebSocketSession player1, WebSocketSession player2, List<Question> gauntlet) {
        this.player1 = player1;
        this.player2 = player2;
        this.gauntlet = gauntlet;
        this.currentQuestionIndex = 0; // Always start at question 0
        this.player1Score = 0;
        this.player2Score = 0;
    }

    // --- Getters ---
    public WebSocketSession getPlayer1() { return player1; }
    public WebSocketSession getPlayer2() { return player2; }
    public List<Question> getGauntlet() { return gauntlet; }
    public int getPlayer1Score() { return player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    
    public Question getCurrentQuestion() {
        return gauntlet.get(currentQuestionIndex);
    }
    
    // --- Game Actions ---
    public void advanceQuestion() {
        this.currentQuestionIndex++;
    }
    
    public void addPoint(WebSocketSession session) {
        if (session.getId().equals(player1.getId())) player1Score++;
        else player2Score++;
    }
    
    public boolean isGameOver() {
        return currentQuestionIndex >= gauntlet.size();
    }
}