package com.example.model;

import java.util.Objects;

import com.example.model.Player.PlayerColor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Movement {
    private int x;
    private int y;

    public Movement() {
    }

    @JsonCreator
    public Movement(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }

    // Getters and Setters for x and y
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Moves are mirrored for the Blue Player
    public int getX(PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLUE) {
            return x * -1;
        } else {
            return x;
        }
    }

    // Moves are mirrored for the Blue Player
    public int getY(PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLUE) {
            return y * -1;
        } else {
            return y;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Movement movement = (Movement) o;
        return x == movement.x && y == movement.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
