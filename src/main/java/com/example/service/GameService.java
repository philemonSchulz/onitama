package com.example.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.example.aimodels.MCTS;
import com.example.aimodels.RandomAi;
import com.example.controller.MoveController;
import com.example.model.Card;
import com.example.model.Game;
import com.example.model.Player;
import com.example.model.Game.GameState;
import com.example.model.GameStats;
import com.example.model.Move;
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

    public GameStats aiVsAi(AiType aiType1, AiType aiType2) {
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
        return new GameStats(game.getCurrentPlayer(), System.currentTimeMillis() - game.getBeginTime());
    }

    public Move getMoveForServerGame(Game game) {
        if (game.getCurrentPlayer().isAi()) {
            return generateAiMove(game);
        } else {
            return generateOwnMove(game);
        }
    }

    /**
     * Custom Method do allow for specific tests
     */
    public void runCustomTests() {
        int redwins = 0;
        int bluewins = 0;
        int overallMatches = 0;
        int abortedMatches = 0;
        long startTime = System.currentTimeMillis();
        long duration = 90 * 60 * 1000;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        while (System.currentTimeMillis() - startTime < duration) {
            Future<GameStats> future = executorService.submit(() -> aiVsAi(AiType.MCTS, AiType.RANDOM_PRIOTIZING));

            try {
                // Wait for the AI vs AI match to complete, with a timeout of 3 minutes
                GameStats stats = future.get(6, TimeUnit.MINUTES);
                if (stats.getWinner().getColor() == PlayerColor.RED) {
                    redwins++;
                    overallMatches++;
                    System.out.println("Red wins: " + redwins + ", Blue wins: " + bluewins + ", Overall: "
                            + overallMatches + ", Aborted: " + abortedMatches);
                } else {
                    bluewins++;
                    overallMatches++;
                    System.out.println("Red wins: " + redwins + ", Blue wins: " + bluewins + ", Overall: "
                            + overallMatches + ", Aborted: " + abortedMatches);
                }
            } catch (TimeoutException e) {
                // Match took too long, so we cancel the task and move to the next one
                future.cancel(true);
                abortedMatches++;
                overallMatches++;
                System.out.println("A match took too long and was aborted.");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println("Red wins: " + redwins + ", Blue wins: " + bluewins);
    }

    /*
     * Methods for Game Logic from here:
     */

    public void playAiMove(Game game) {
        if (game.getGameState() == GameState.IN_PROGRESS && game.getCurrentPlayer().isAi()) {
            Move move = generateAiMove(game);
            processMove(game, move);
            switchTurn(game);
        }
    }

    public void playOwnMove(Game game) {
        if (game.getGameState() == GameState.IN_PROGRESS && !game.getCurrentPlayer().isAi()) {
            Move move = generateOwnMove(game);
            processMove(game, move);
            switchTurn(game);
        }
    }

    public GameStats runRandomGame(Game game) {
        while (game.getGameState() == GameState.IN_PROGRESS) {
            Move move = RandomAi.getMove(game, true);
            processMove(game, move);
            switchTurn(game);
        }
        return new GameStats(game.getCurrentPlayer(), System.currentTimeMillis() - game.getBeginTime());
    }

    public boolean processMove(Game game, Move move) {
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

        if (tile.getPiece() == null) {
            System.out.println("Invalid move. No piece on tile.");
        }
        Piece piece = tile.removePiece();
        targetTile.setPiece(piece);

        if (targetTile.isTempleReached() || removedPieceWasMaster) {
            game.setGameState(playerColor == PlayerColor.RED ? GameState.FINISHED : GameState.FINISHED);
        }

        ArrayList<Card> cards = playerColor == PlayerColor.RED ? game.getPlayerRedCards()
                : game.getPlayerBlueCards();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == move.getCard()) {
                cards.set(i, game.getNextCard());
                game.setNextCard(move.getCard());
                break;
            }
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

    private Move generateAiMove(Game game) {
        Move move = null;
        switch (game.getCurrentPlayer().getAiType()) {
            case RANDOM -> move = RandomAi.getMove(game, false);
            case RANDOM_PRIOTIZING -> move = RandomAi.getMove(game, true);
            case MCTS -> {
                MCTS mcts = new MCTS();
                move = mcts.uctSearch(game, true,
                        game.getCurrentPlayer().getColor() == PlayerColor.RED ? 0.7 : 0.7);
            }
            default -> move = RandomAi.getMove(game, false);
        }

        return move;
    }

    private Move generateOwnMove(Game game) {
        game.getBoard().printBoard();
        PlayerColor playerColor = game.getCurrentPlayer().getColor();
        LinkedList<Move> allPossibleMoves = MoveController.getAllPossibleMovesAsObject(game).getAllMoves();
        System.out.println("Choose a move:");
        for (int i = 0; i < allPossibleMoves.size(); i++) {
            Move move = allPossibleMoves.get(i);
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
