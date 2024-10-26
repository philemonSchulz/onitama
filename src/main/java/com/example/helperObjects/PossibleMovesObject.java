package com.example.helperObjects;

import java.util.LinkedList;

import com.example.model.Move;

public class PossibleMovesObject {
    private LinkedList<Move> winningMoves;
    private LinkedList<Move> capturingMoves;
    private LinkedList<Move> normalMoves;

    public PossibleMovesObject(LinkedList<Move> winningMoves, LinkedList<Move> capturingMoves,
            LinkedList<Move> normalMoves) {
        this.winningMoves = winningMoves;
        this.capturingMoves = capturingMoves;
        this.normalMoves = normalMoves;
    }

    public LinkedList<Move> getWinningMoves() {
        return winningMoves;
    }

    public LinkedList<Move> getCapturingMoves() {
        return capturingMoves;
    }

    public LinkedList<Move> getNormalMoves() {
        return normalMoves;
    }

    public LinkedList<Move> getAllMoves() {
        LinkedList<Move> allMoves = new LinkedList<>();
        allMoves.addAll(winningMoves);
        allMoves.addAll(capturingMoves);
        allMoves.addAll(normalMoves);
        return allMoves;
    }
}
