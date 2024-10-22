package com.example.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Piece {
    public enum PieceType {
        STUDENT, MASTER
    }

    private PieceType type;
    private Player.PlayerColor color;
    private String name;
    private Integer x;
    private Integer y;

    public Piece() {
        // Default constructor for deserialization
    }

    @JsonCreator
    public Piece(@JsonProperty("type") PieceType type, @JsonProperty("color") Player.PlayerColor color,
            @JsonProperty("name") String name, @JsonProperty("x") Integer x, @JsonProperty("y") Integer y) {
        this.type = type;
        this.color = color;
        this.name = name;
        this.x = x;
        this.y = y;
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

    // Getters and setters for all fields
    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public Player.PlayerColor getColor() {
        return color;
    }

    public void setColor(Player.PlayerColor color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Piece piece = (Piece) o;
        return type == piece.type &&
                color == piece.color &&
                Objects.equals(name, piece.name) &&
                Objects.equals(x, piece.x) &&
                Objects.equals(y, piece.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, name, x, y);
    }
}
