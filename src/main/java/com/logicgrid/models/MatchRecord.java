package com.logicgrid.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "match_history")
public class MatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User player;

    @Column(name = "opponent_name")
    private String opponentName;

    @Column(name = "result")
    private String result;

    @Column(name = "elo_change")
    private int eloChange;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "match_date")
    private Date matchDate;

    // --- Constructors ---
    public MatchRecord() {}

    public MatchRecord(User player, String opponentName, String result, int eloChange) {
        this.player = player;
        this.opponentName = opponentName;
        this.result = result;
        this.eloChange = eloChange;
        this.matchDate = new Date(); // Automatically sets the exact moment the match ends
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getPlayer() { return player; }
    public void setPlayer(User player) { this.player = player; }
    public String getOpponentName() { return opponentName; }
    public void setOpponentName(String opponentName) { this.opponentName = opponentName; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public int getEloChange() { return eloChange; }
    public void setEloChange(int eloChange) { this.eloChange = eloChange; }
    public Date getMatchDate() { return matchDate; }
    public void setMatchDate(Date matchDate) { this.matchDate = matchDate; }
}