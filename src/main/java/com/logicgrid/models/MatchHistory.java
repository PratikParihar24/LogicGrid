package com.logicgrid.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "match_history")
public class MatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private int matchId;

    @Column(name = "player1_username", nullable = false)
    private String player1Username;

    @Column(name = "player2_username", nullable = false)
    private String player2Username;

    @Column(name = "winner_username")
    private String winnerUsername;

    @Column(name = "puzzle_id", nullable = false)
    private int puzzleId;

    @Column(name = "played_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date playedAt = new Date();

    // --- Empty Constructor required by Hibernate ---
    public MatchHistory() {}

    // --- Getters and Setters ---
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) { this.player1Username = player1Username; }

    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) { this.player2Username = player2Username; }

    public String getWinnerUsername() { return winnerUsername; }
    public void setWinnerUsername(String winnerUsername) { this.winnerUsername = winnerUsername; }

    public int getPuzzleId() { return puzzleId; }
    public void setPuzzleId(int puzzleId) { this.puzzleId = puzzleId; }

    public Date getPlayedAt() { return playedAt; }
    public void setPlayedAt(Date playedAt) { this.playedAt = playedAt; }
}