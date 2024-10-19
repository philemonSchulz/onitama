package com.example.model;

import com.example.model.Player.PlayerColor;

public class GameStats {
    private Player winner;
    private long duration;

    public GameStats(Player winner, long duration) {
        this.winner = winner;
        this.duration = duration;
    }

    public Player getWinner() {
        return winner;
    }

    public long getDuration() {
        return duration;
    }
}
