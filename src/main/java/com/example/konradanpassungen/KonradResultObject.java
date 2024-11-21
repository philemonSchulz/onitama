package com.example.konradanpassungen;

import com.example.model.Game;
import com.example.model.Player.PlayerColor;

public class KonradResultObject {
    private String winner;
    private double duration;
    private boolean isTempleWin;

    public KonradResultObject(Game game) {
        this.winner = game.getCurrentPlayer().getColor() == PlayerColor.RED ? "RED" : "BLUE";
        this.duration = System.currentTimeMillis() - game.getBeginTime();
        this.isTempleWin = game.isWinTroughTemple();
    }

    public String getWinner() {
        return winner;
    }

    public double getDuration() {
        return duration;
    }

    public boolean isTempleWin() {
        return isTempleWin;
    }
}
