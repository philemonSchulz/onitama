package com.example.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Move {
    private Movement movement;
    private Piece piece;
    private Piece capturedPiece;
    private Card card;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Move move = (Move) o;
        return Objects.equals(this.movement, move.movement) &&
                Objects.equals(this.card, move.card) &&
                Objects.equals(this.piece, move.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movement, card, piece);
    }

    public Move() {
        // Default constructor for deserialization
    }

    public Move(Move move) {
        this.movement = move.movement;
        this.piece = new Piece(move.piece);
        this.capturedPiece = move.capturedPiece == null ? null : new Piece(move.capturedPiece);
        this.card = move.card;
    }

    @JsonCreator
    public Move(@JsonProperty("move") Movement move, @JsonProperty("piece") Piece piece,
            @JsonProperty("capturedPiece") Piece capturedPiece, @JsonProperty("card") Card card) {
        this.movement = move;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.card = card;
    }

    public Movement getMovement() {
        return movement;
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
