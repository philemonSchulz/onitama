package com.example.model;

public class Player {
    public enum PlayerColor {
        RED, BLUE
    }

    public enum AiType {
        RANDOM, RANDOM_PRIOTIZING, MCTS
    }

    private PlayerColor color;
    private AiType aiType;

    public Player(PlayerColor color) {
        this.color = color;
    }

    public Player(PlayerColor color, AiType aiType) {
        this.color = color;
        this.aiType = aiType;
    }

    public PlayerColor getColor() {
        return color;
    }

    public AiType getAiType() {
        return aiType;
    }

    public boolean isPlayerRed() {
        return color == PlayerColor.RED;
    }

    public boolean isPlayerBlue() {
        return color == PlayerColor.BLUE;
    }

    public boolean isAi() {
        return aiType != null;
    }
}
