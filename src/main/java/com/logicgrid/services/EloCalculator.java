package com.logicgrid.services;

import com.logicgrid.models.User;

public class EloCalculator {

    // K-Factor determines how drastically scores change in a single match. 
    // 32 is standard for new/active players.
    private static final int K_FACTOR = 32;

    public static void updateRatings(User winner, User loser) {
        double winnerExpected = getExpectedScore(winner.getEloRating(), loser.getEloRating());
        double loserExpected = getExpectedScore(loser.getEloRating(), winner.getEloRating());

        // Actual score: 1 for win, 0 for loss
        int newWinnerElo = (int) (winner.getEloRating() + K_FACTOR * (1.0 - winnerExpected));
        int newLoserElo = (int) (loser.getEloRating() + K_FACTOR * (0.0 - loserExpected));

        // Prevent Elo from dropping below 0
        if (newLoserElo < 0) newLoserElo = 0;

        winner.setEloRating(newWinnerElo);
        loser.setEloRating(newLoserElo);
        
        System.out.println("ELO ENGINE: " + winner.getUsername() + " goes to " + newWinnerElo + 
                           " | " + loser.getUsername() + " drops to " + newLoserElo);
    }

    private static double getExpectedScore(int ratingA, int ratingB) {
        return 1.0 / (1.0 + Math.pow(10, (ratingB - ratingA) / 400.0));
    }
}