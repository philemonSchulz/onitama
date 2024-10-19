package com.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Move {
    private Movement move;
    private Piece piece;
    private Piece capturedPiece;
    private Card card;

    public Move() {
        // Default constructor for deserialization
    }

    @JsonCreator
    public Move(@JsonProperty("move") Movement move, @JsonProperty("piece") Piece piece,
            @JsonProperty("capturedPiece") Piece capturedPiece, @JsonProperty("card") Card card) {
        this.move = move;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.card = card;
    }

    public Movement getMove() {
        return move;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public Card getCard() {
        return card;
    }

    public boolean capturesPiece() {
        return capturedPiece != null;
    }
}
