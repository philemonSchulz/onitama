package com.example.model;

import com.example.model.Piece.PieceType;
import com.example.model.Player.PlayerColor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tile {
    private int x;
    private int y;
    private PlayerColor templeColor;
    private Piece piece;

    @JsonCreator
    public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y,
            @JsonProperty("templeColor") PlayerColor templeColor, @JsonProperty("piece") Piece piece) {
        this.x = x;
        this.y = y;
        this.templeColor = templeColor;
        this.piece = piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        piece.setPosition(this.x, this.y);
    }

    public Piece removePiece() {
        Piece piece = this.piece;
        piece.clearPosition();
        this.piece = null;
        return piece;
    }

    public boolean isTempleReached() {
        return templeColor != null && piece != null && piece.getColor() != templeColor
                && piece.getType() == PieceType.MASTER;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece.PieceType getPieceType() {
        return piece == null ? null : piece.getType();
    }

    public PlayerColor getPieceColor() {
        return piece == null ? null : piece.getColor();
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public boolean isTemple() {
        return templeColor != null;
    }

    public boolean isOpponentsTemple(PlayerColor color) {
        return templeColor != null && templeColor != color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
