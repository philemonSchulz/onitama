package com.example.aimodels;

import java.util.LinkedList;

import com.example.controller.MoveController;
import com.example.model.Game;
import com.example.model.Move;
import com.example.model.PossibleMovesObject;
import com.example.model.Player.PlayerColor;

public class RandomAi {

    public static Move getMove(Game game, boolean priotizeCapturingMoves) {
        PossibleMovesObject moves = MoveController.getAllPossibleMovesAsObject(game);
        if (moves.getAllMoves().size() == 0) {
            System.out.println("No possible moves");
        }
        if (priotizeCapturingMoves) {
            if (moves.getWinningMoves().size() > 0) {
                return moves.getWinningMoves().getFirst();
            } else if (moves.getCapturingMoves().size() > 0) {
                LinkedList<Move> allPossibleMoves = moves.getCapturingMoves();
                int randomMoveIndex = (int) (Math.random() * allPossibleMoves.size());
                return allPossibleMoves.get(randomMoveIndex);
            } else {
                LinkedList<Move> allPossibleMoves = moves.getAllMoves();
                int randomMoveIndex = (int) (Math.random() * allPossibleMoves.size());
                return allPossibleMoves.get(randomMoveIndex);
            }
        } else {
            LinkedList<Move> allPossibleMoves = moves.getAllMoves();
            int randomMoveIndex = (int) (Math.random() * allPossibleMoves.size());
            return allPossibleMoves.get(randomMoveIndex);
        }

    }
}
