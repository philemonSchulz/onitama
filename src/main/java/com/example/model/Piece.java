package com.example.model;

public class Piece {
    public enum PieceType {
        STUDENT, MASTER
    }

    private PieceType type;
    private Player.PlayerColor color;
    private String name;
    private Integer x;
    private Integer y;

    public Piece(PieceType type, Player.PlayerColor color, String name) {
        this.type = type;
        this.color = color;
        this.name = name;
    }

    public Piece(Piece piece) {
        this.type = piece.type;
        this.color = piece.color;
        this.name = piece.name;
        this.x = piece.x;
        this.y = piece.y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void clearPosition() {
        this.x = null;
        this.y = null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PieceType getType() {
        return type;
    }

    public Player.PlayerColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
