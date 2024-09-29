package com.example.model;

public class MoveObject {
    private Move move;
    private Piece piece;
    private Piece capturedPiece;
    private Card card;

    public MoveObject(Move move, Piece piece, Piece capturedPiece, Card card) {
        this.move = move;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.card = card;
    }

    public Move getMove() {
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
