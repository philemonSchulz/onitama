package com.example.aimodels;

import java.util.LinkedList;

import com.example.controller.MoveController;
import com.example.model.Game;
import com.example.model.Move;
import com.example.model.Player.PlayerColor;

public class RandomAi {

    public static Move getMove(Game game, boolean priotizeCapturingMoves) {
        LinkedList<Move> allPossibleMoves = MoveController.getAllPossibleMoves(game);
        if (priotizeCapturingMoves) {
            return allPossibleMoves.getFirst();
        } else {
            int randomMoveIndex = (int) (Math.random() * allPossibleMoves.size());
            return allPossibleMoves.get(randomMoveIndex);
        }

    }
}
