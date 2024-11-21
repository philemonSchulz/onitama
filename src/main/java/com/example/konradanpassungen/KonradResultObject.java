package com.example.konradanpassungen;

import com.example.model.Game;
import com.example.model.Player.PlayerColor;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KonradResultObject {
    @JsonProperty("winner")
    private String winner;

    @JsonProperty("duration")
    private double duration;

    @JsonProperty("isTempleWin")
    private boolean isTempleWin;

    public KonradResultObject() {
    }

    public KonradResultObject(Game game) {
        this.winner = game.getCurrentPlayer().getColor() == PlayerColor.RED ? "RED" : "BLUE";
        this.duration = System.currentTimeMillis() - game.getBeginTime();
        this.isTempleWin = game.isWinTroughTemple();
    }

    public KonradResultObject(String winner, double duration, boolean isTempleWin) {
        this.winner = winner;
        this.duration = duration;
        this.isTempleWin = isTempleWin;
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
