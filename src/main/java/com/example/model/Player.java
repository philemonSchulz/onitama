package com.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {
    public enum PlayerColor {
        RED, BLUE
    }

    public enum AiType {
        RANDOM, RANDOM_PRIOTIZING, HEURISTIC, MCTS, RAVE_MCTS, HEURISTIC_MCTS, RAVE_TEST
    }

    private PlayerColor color;
    private AiType aiType;

    public Player() {
        // Default constructor for deserialization
    }

    @JsonCreator
    public Player(@JsonProperty("color") PlayerColor color, @JsonProperty("aiType") AiType aiType) {
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
