package com.example.aimodels;

import java.util.HashMap;
import java.util.Map;

import com.example.controller.MoveController;
import com.example.helperObjects.PossibleMovesObject;
import com.example.model.Game;
import com.example.model.Move;
import com.example.model.Piece;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class HeuristicAi {
    private static final double PieceWeight = 1;
    private static final double PositionWeight = 1;
    private static final double MobilityWeight = 1;

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
        Move bestMove = moveValues.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                .orElse(null);
        if (false) {
            game.getBoard().printBoard();
            for (Move move : moveValues.keySet()) {
                System.out.println(move.getPiece().getName() + " " + move.getMovement().getX(currentPlayerColor) + " "
                        + move.getMovement().getY(currentPlayerColor) + " Value: " + moveValues.get(move));
            }
            System.out.println("Selecting move: "
                    + bestMove.getPiece().getName() + " " + bestMove.getMovement().getX(currentPlayerColor) + " "
                    + bestMove.getMovement().getY(currentPlayerColor));
        }

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
            value += (gameCopy.getPlayerRedPieces().size() - gameCopy.getPlayerBluePieces().size()) * PieceWeight;
        } else {
            value += (gameCopy.getPlayerBluePieces().size() - gameCopy.getPlayerRedPieces().size()) * PieceWeight;
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
                    value += (1 * PositionWeight);
                    break;
                case 1:
                    value -= (1 * PositionWeight);
                    break;
                case 2:
                    value -= (2 * PositionWeight);
                    break;
                default:
                    break;
            }
        }

        // Evaluate the mobility for the current players pieces
        int a = initialPossibleMoves.getAllMoves().size();
        int b = MoveController.getAllPossibleMovesAsObject(gameCopy).getAllMoves().size();
        value += (MoveController.getAllPossibleMovesAsObject(gameCopy).getAllMoves().size()
                - initialPossibleMoves.getAllMoves().size()) * MobilityWeight;

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
