package com.example.model;

public class MCTSMoveObject {
    private Move move;
    private double iterations;

    public MCTSMoveObject(Move move, double iterations) {
        this.move = move;
        this.iterations = iterations;
    }

    public Move getMove() {
        return move;
    }

    public double getIterations() {
        return iterations;
    }
}
