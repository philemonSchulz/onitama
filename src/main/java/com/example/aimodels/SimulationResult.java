package com.example.aimodels;

import java.util.List;

import com.example.model.Move;

public class SimulationResult {
    private int reward;
    private List<Move> playedMoves;

    public SimulationResult(int reward, List<Move> playedMoves) {
        this.reward = reward;
        this.playedMoves = playedMoves;
    }

    public int getReward() {
        return reward;
    }

    public List<Move> getPlayedMoves() {
        return playedMoves;
    }
}
