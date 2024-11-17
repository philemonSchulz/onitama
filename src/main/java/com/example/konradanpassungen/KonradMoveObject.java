package com.example.konradanpassungen;

import com.example.model.Move;

public class KonradMoveObject {
    private int x;
    private int y;
    private int movementX;
    private int movementY;
    private String cardName;

    public KonradMoveObject() {
    }

    public KonradMoveObject(Move move) {
        this.x = 6 - move.getPiece().getY();
        this.y = 6 - move.getPiece().getX();
        this.movementX = -1 * move.getMovement().getY();
        this.movementY = -1 * move.getMovement().getX();
        this.cardName = move.getCard().getName();
    }

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

    public int getMovementX() {
        return movementX;
    }

    public void setMovementX(int movementX) {
        this.movementX = movementX;
    }

    public int getMovementY() {
        return movementY;
    }

    public void setMovementY(int movementY) {
        this.movementY = movementY;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
