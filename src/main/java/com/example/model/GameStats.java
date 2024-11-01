package com.example.model;

import com.example.model.Player.PlayerColor;

public class GameStats {
    private Player winner;
    private long duration;
    private int pieceCountWinner;
    private int pieceCountLoser;
    private boolean winThroughTemple;

    public GameStats(Player winner, long duration) {
        this.winner = winner;
        this.duration = duration;
    }

    public GameStats(Player winner, long duration, int pieceCountWinner, int pieceCountLoser,
            boolean winThroughTemple) {
        this.winner = winner;
        this.duration = duration;
        this.pieceCountWinner = pieceCountWinner;
        this.pieceCountLoser = pieceCountLoser;
        this.winThroughTemple = winThroughTemple;
    }

    public Player getWinner() {
        return winner;
    }

    public long getDuration() {
        return duration;
    }

    public int getPieceCountWinner() {
        return pieceCountWinner;
    }

    public int getPieceCountLoser() {
        return pieceCountLoser;
    }

    public boolean isWinThroughTemple() {
        return winThroughTemple;
    }
}
