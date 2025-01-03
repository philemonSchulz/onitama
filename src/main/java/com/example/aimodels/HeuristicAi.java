package com.example.aimodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.controller.MoveController;
import com.example.helperObjects.PossibleMovesObject;
import com.example.model.Game;
import com.example.model.Move;
import com.example.model.Piece;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class HeuristicAi {

    private HashMap<Move, Double> moveValues;
    private Game game;
    private PlayerColor currentPlayerColor;
    private PossibleMovesObject initialPossibleMoves;
    private GameService gameService;

    public HeuristicAi(Game game) {
        this.moveValues = new HashMap<>();
        this.game = game;
        this.currentPlayerColor = game.getCurrentPlayer().getColor();
        this.initialPossibleMoves = MoveController.getAllPossibleMovesAsObject(game);
        this.gameService = new GameService();
    }

    public Move getMove() {
        if (initialPossibleMoves.getAllMoves().size() == 0) {
            System.out.println("No possible moves");
            return null;
        }

        if (initialPossibleMoves.getWinningMoves().size() > 0) {
            return initialPossibleMoves.getWinningMoves().getFirst();
        }

        for (Move move : initialPossibleMoves.getAllMoves()) {
            double value = evaluateMove(move);
            moveValues.put(move, value);
        }
        List<Move> result = new ArrayList<>();
        double max = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Move, Double> entry : moveValues.entrySet()) {
            double value = entry.getValue();
            if (value > max) {
                max = value;
                result.clear();
                result.add(entry.getKey());
            } else if (value == max) {
                result.add(entry.getKey());
            }
        }
        Move bestMove = result.get((int) (Math.random() * result.size()));

        return bestMove;
    }

    private double evaluateMove(Move move) {
        if (checkMoveForLoss(move)) {
            return Double.NEGATIVE_INFINITY;
        }

        double value = 0;
        Game gameCopy = new Game(game);

        gameService.processMove(gameCopy, move);

        // Evaluate the number of pieces for each player
        if (currentPlayerColor == PlayerColor.RED) {
            value += (gameCopy.getPlayerRedPieces().size() - gameCopy.getPlayerBluePieces().size());
        } else {
            value += (gameCopy.getPlayerBluePieces().size() - gameCopy.getPlayerRedPieces().size());
        }

        // Evaluate the positioning on the board for the current players pieces
        for (Piece piece : (currentPlayerColor == PlayerColor.RED ? gameCopy.getPlayerRedPieces()
                : gameCopy.getPlayerBluePieces())) {
            int helper = 0;
            if (piece.getX() == 0 || piece.getX() == 6) {
                helper++;
            }
            if (piece.getY() == 0 || piece.getY() == 6) {
                helper++;
            }
            switch (helper) {
                case 0:
                    value += 1;
                    break;
                case 1:
                    value -= 1;
                    break;
                case 2:
                    value -= 2;
                    break;
                default:
                    break;
            }
        }

        // Evaluate the mobility for the current players pieces
        value += (MoveController.getAllPossibleMovesAsObject(gameCopy).getAllMoves().size()
                - initialPossibleMoves.getAllMoves().size());

        return value;
    }

    private boolean checkMoveForLoss(Move move) {
        Game gameCopy = new Game(game);
        gameService.processMove(gameCopy, move);
        gameService.switchTurn(gameCopy);
        PossibleMovesObject moves = MoveController.getAllPossibleMovesAsObject(gameCopy);
        if (moves.getWinningMoves().size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
