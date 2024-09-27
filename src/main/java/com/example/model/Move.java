package com.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class Move {
    private int x;
    private int y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX(boolean invert) {
        if (invert) {
            return x * -1;
        } else {
            return x;
        }
    }

    public int getY(boolean invert) {
        if (invert) {
            return y * -1;
        } else {
            return y;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
