package com.example.model;

import java.util.HashSet;
import java.util.Set;

import com.example.model.Player.PlayerColor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {
    private Set<Movement> moves;
    private String name;
    private PlayerColor color;

    public Card() {
        this.moves = new HashSet<>();
    }

    @JsonCreator
    public Card(@JsonProperty("name") String name, @JsonProperty("color") PlayerColor color) {
        this.name = name;
        this.color = color;
        this.moves = new HashSet<>();
    }

    public Card(Card card) {
        this.name = card.name;
        this.color = card.color;
        this.moves = new HashSet<>(card.moves);
    }

    public void addMove(int x, int y) {
        moves.add(new Movement(x, y));
    }

    @JsonProperty("moves")
    public Set<Movement> getMoves() {
        return moves; // Return as Set
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("color")
    public PlayerColor getColor() {
        return color;
    }

    public HashSet<Movement> getAllowedMoves(int x, int y, PlayerColor playerColor) {
        HashSet<Movement> allowedMoves = new HashSet<>();
        for (Movement move : moves) {
            if (x + move.getX(playerColor) >= 0
                    && x + move.getX(playerColor) < Board.BOARD_SIZE
                    && y + move.getY(playerColor) >= 0
                    && y + move.getY(playerColor) < Board.BOARD_SIZE) {
                // Send back the original move, not the mirrored one
                allowedMoves.add(new Movement(move.getX(PlayerColor.RED), move.getY(PlayerColor.RED)));
            }
        }
        return allowedMoves;
    }
}
