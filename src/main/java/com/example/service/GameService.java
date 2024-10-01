package com.example.service;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;

import com.example.aimodels.RandomAi;
import com.example.controller.MoveController;
import com.example.model.Game;
import com.example.model.Player;
import com.example.model.Game.GameState;
import com.example.model.MoveObject;
import com.example.model.Piece;
import com.example.model.Piece.PieceType;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;
import com.example.model.Tile;

public class GameService {
    public boolean playerVsAi(AiType aiType) {
        Game game = new Game(UUID.randomUUID().toString());
        game.setPlayerRed(new Player(PlayerColor.RED, null));
        game.setPlayerBlue(new Player(PlayerColor.BLUE, aiType));
        game.setBeginTime(System.currentTimeMillis());
        game.setGameState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        while (game.getGameState() == GameState.IN_PROGRESS) {
            if (game.getCurrentPlayer().getColor() == Player.PlayerColor.RED) {
                playOwnMove(game);
            } else {
                playAiMove(game);
            }
        }
        if (game.getGameState() == GameState.FINISHED) {
            System.out.println("Game finished. Winner: " + game.getCurrentPlayer().getColor());
        }
        return true;
    }

    public boolean aiVsAi(AiType aiType1, AiType aiType2) {
        Game game = new Game(UUID.randomUUID().toString());
        game.setPlayerRed(new Player(PlayerColor.RED, aiType1));
        game.setPlayerBlue(new Player(PlayerColor.BLUE, aiType2));
        game.setBeginTime(System.currentTimeMillis());
        game.setGameState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        while (game.getGameState() == GameState.IN_PROGRESS) {
            playAiMove(game);
        }
        if (game.getGameState() == GameState.FINISHED) {
            System.out.println("Game finished. Winner: " + game.getCurrentPlayer().getColor());
        }
        return true;
    }

    public MoveObject getMoveForServerGame(Game game) {
        if (game.getCurrentPlayer().isAi()) {
            return generateAiMove(game);
        } else {
            return generateOwnMove(game);
        }
    }

    /*
     * Methods for Game Logic from here:
     */

    public void playAiMove(Game game) {
        if (game.getGameState() == GameState.IN_PROGRESS && game.getCurrentPlayer().isAi()) {
            MoveObject move = generateAiMove(game);
            processMove(game, move);
            switchTurn(game);
        }
    }

    public void playOwnMove(Game game) {
        if (game.getGameState() == GameState.IN_PROGRESS && !game.getCurrentPlayer().isAi()) {
            MoveObject move = generateOwnMove(game);
            processMove(game, move);
            switchTurn(game);
        }
    }

    public boolean processMove(Game game, MoveObject move) {
        PlayerColor playerColor = game.getCurrentPlayer().getColor();
        Tile tile = game.getBoard().getTile(move.getPiece().getX(), move.getPiece().getY());
        Tile targetTile = game.getBoard().getTile(move.getPiece().getX() + move.getMove().getX(playerColor),
                move.getPiece().getY() + move.getMove().getY(playerColor));

        boolean removedPieceWasMaster = false;

        if (targetTile.getPiece() != null && targetTile.getPiece().getColor() != playerColor) {
            if (playerColor == PlayerColor.RED) {
                game.getPlayerBluePieces().remove(targetTile.getPiece());
            } else {
                game.getPlayerRedPieces().remove(targetTile.getPiece());
            }
            if (targetTile.getPiece().getType() == PieceType.MASTER) {
                removedPieceWasMaster = true;
            }
            targetTile.removePiece();
        } else if (targetTile.getPiece() != null && targetTile.getPiece().getColor() == playerColor) {
            return false;
        }

        Piece piece = tile.removePiece();
        targetTile.setPiece(piece);

        if (targetTile.isTempleReached() || removedPieceWasMaster) {
            game.setGameState(playerColor == PlayerColor.RED ? GameState.FINISHED : GameState.FINISHED);
        }

        return true;
    }

    public void switchTurn(Game game) {
        if (game.getGameState() == GameState.IN_PROGRESS) {
            game.setCurrentPlayer(game.getCurrentPlayer().getColor() == Player.PlayerColor.RED ? game.getPlayerBlue()
                    : game.getPlayerRed());
            game.setTurnStartTime(System.currentTimeMillis());
        }
    }

    private MoveObject generateAiMove(Game game) {
        switch (game.getCurrentPlayer().getAiType()) {
            case RANDOM:
                return RandomAi.getMove(game, game.getCurrentPlayer().getColor(), false);
            case RANDOM_PRIOTIZING:
                return RandomAi.getMove(game, game.getCurrentPlayer().getColor(), true);
            case MCTS:
            default:
                return RandomAi.getMove(game, game.getCurrentPlayer().getColor(), false);
        }
    }

    private MoveObject generateOwnMove(Game game) {
        game.getBoard().printBoard();
        PlayerColor playerColor = game.getCurrentPlayer().getColor();
        LinkedList<MoveObject> allPossibleMoves = MoveController.getAllPossibleMoves(game, playerColor);
        System.out.println("Choose a move:");
        for (int i = 0; i < allPossibleMoves.size(); i++) {
            MoveObject move = allPossibleMoves.get(i);
            System.out.println(i + ": " + move.capturesPiece() + ", Gegner: "
                    + (move.getCapturedPiece() != null ? move.getCapturedPiece().getName() : "null") + ",  Figure: "
                    + move.getPiece().getName()
                    + ", Move: " + move.getMove().getX(playerColor) + ", " + move.getMove().getY(playerColor));
        }
        Scanner scanner = new Scanner(System.in);
        int moveIndex = scanner.nextInt();

        while (moveIndex < 0 || moveIndex >= allPossibleMoves.size()) {
            System.out.println("Invalid index. Please select a valid Move index:");
            moveIndex = scanner.nextInt();
        }
        return allPossibleMoves.get(moveIndex);
    }

    public boolean isPlayerTurn(Game game, PlayerColor playerColor) {
        return game.getCurrentPlayer().getColor() == playerColor;
    }

    public boolean hasTimeExpired(Game game) {
        long elapsedTime = System.currentTimeMillis() - game.getTurnStartTime();
        return elapsedTime > 30000;
    }
}
