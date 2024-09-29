package com.example.model;

import java.util.ArrayList;

import com.example.service.CardCreator;
import com.example.model.Player.PlayerColor;

public class Game {
    public enum GameState {
        WAITING_FOR_PLAYERS, IN_PROGRESS, FINISHED
    }

    private String gameId;
    private long beginTime;
    private long turnStartTime;
    private GameState gameState;
    private Board board;
    private Player playerRed;
    private Player playerBlue;
    private Player currentPlayer;
    private ArrayList<Card> playerRedCards;
    private ArrayList<Card> playerBlueCards;
    private ArrayList<Piece> playerRedPieces;
    private ArrayList<Piece> playerBluePieces;
    private Card nextCard;

    public Game(String gameId) {
        this.gameId = gameId;
        this.board = new Board();
        Card[] cards = CardCreator.getFiveRandomCards();
        this.playerRedCards = new ArrayList<>();
        this.playerBlueCards = new ArrayList<>();
        this.playerRedCards.add(cards[0]);
        this.playerRedCards.add(cards[1]);
        this.playerBlueCards.add(cards[2]);
        this.playerBlueCards.add(cards[3]);
        this.nextCard = cards[4];
        this.playerRedPieces = createFigures(PlayerColor.RED);
        this.playerBluePieces = createFigures(PlayerColor.BLUE);
        setFigures();
    }

    public ArrayList<Piece> createFigures(PlayerColor playerColor) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            if (i == (Board.BOARD_SIZE / 2)) {
                pieces.add(
                        new Piece(Piece.PieceType.MASTER, playerColor, playerColor == PlayerColor.RED ? "RM" : "BM"));
            } else {
                pieces.add(new Piece(Piece.PieceType.STUDENT, playerColor,
                        playerColor == PlayerColor.RED ? "R" + i : "B" + i));
            }
        }
        return pieces;
    }

    public void setFigures() {
        for (int x = 0; x < Board.BOARD_SIZE; x++) {
            for (int y = 0; y < Board.BOARD_SIZE; y++) {
                if (y == 0) {
                    board.getTile(x, y).setPiece(playerRedPieces.get(x));
                } else if (y == Board.BOARD_SIZE - 1) {
                    board.getTile(x, y).setPiece(playerBluePieces.get(Board.BOARD_SIZE - 1 - x));
                }
            }
        }
    }

    public void switchCards(Card card, PlayerColor playerColor) {
        ArrayList<Card> playerCards = playerColor == PlayerColor.RED ? playerRedCards : playerBlueCards;
        for (int i = 0; i < playerCards.size(); i++) {
            if (playerCards.get(i) == card) {
                playerCards.set(i, nextCard);
                nextCard = card;
                break;
            }
        }
    }

    public void setGameId(String id) {
        this.gameId = id;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public void setTurnStartTime(long turnStartTime) {
        this.turnStartTime = turnStartTime;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayerRed(Player playerRed) {
        this.playerRed = playerRed;
    }

    public void setPlayerBlue(Player playerBlue) {
        this.playerBlue = playerBlue;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPlayerRedCards(ArrayList<Card> playerRedCards) {
        this.playerRedCards = playerRedCards;
    }

    public void setPlayerBlueCards(ArrayList<Card> playerBlueCards) {
        this.playerBlueCards = playerBlueCards;
    }

    public void setPlayerRedPieces(ArrayList<Piece> playerRedPieces) {
        this.playerRedPieces = playerRedPieces;
    }

    public void setPlayerBluePieces(ArrayList<Piece> playerBluePieces) {
        this.playerBluePieces = playerBluePieces;
    }

    public void setNextCard(Card nextCard) {
        this.nextCard = nextCard;
    }

    public String getGameId() {
        return gameId;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getTurnStartTime() {
        return turnStartTime;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayerRed() {
        return playerRed;
    }

    public Player getPlayerBlue() {
        return playerBlue;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public ArrayList<Card> getPlayerRedCards() {
        return playerRedCards;
    }

    public ArrayList<Card> getPlayerBlueCards() {
        return playerBlueCards;
    }

    public ArrayList<Piece> getPlayerRedPieces() {
        return playerRedPieces;
    }

    public ArrayList<Piece> getPlayerBluePieces() {
        return playerBluePieces;
    }

    public Card getNextCard() {
        return nextCard;
    }
}
