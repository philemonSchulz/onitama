package com.example.model;

import java.util.ArrayList;
import java.util.List;

import com.example.service.CardCreator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private boolean winTroughTemple;

    public Game() {
    }

    @JsonCreator
    public Game(@JsonProperty("gameId") String gameId) {
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
        setPieces();
    }

    public Game(@JsonProperty("gameId") String gameId, boolean custom) {
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
        setCustomPieces();
        board.printBoard();
    }

    public Game(Game game) {
        this.gameId = game.gameId;
        this.beginTime = game.beginTime;
        this.turnStartTime = game.turnStartTime;
        this.gameState = game.gameState;
        this.board = new Board();
        this.playerRed = game.playerRed;
        this.playerBlue = game.playerBlue;
        this.currentPlayer = game.currentPlayer;
        this.playerRedCards = new ArrayList<>(game.playerRedCards);
        this.playerBlueCards = new ArrayList<>(game.playerBlueCards);
        this.playerRedPieces = copyPieces(game.playerRedPieces);
        this.playerBluePieces = copyPieces(game.playerBluePieces);
        this.nextCard = game.nextCard;
    }

    public ArrayList<Piece> createFigures(PlayerColor playerColor) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            if (i == (Board.BOARD_SIZE / 2)) {
                pieces.add(
                        new Piece(Piece.PieceType.MASTER, playerColor, playerColor == PlayerColor.RED ? "RM" : "BM",
                                null, null));
            } else {
                pieces.add(new Piece(Piece.PieceType.STUDENT, playerColor,
                        playerColor == PlayerColor.RED ? "R" + i : "B" + i, null, null));
            }
        }
        return pieces;
    }

    public ArrayList<Piece> copyPieces(ArrayList<Piece> pieces) {
        ArrayList<Piece> newPieces = new ArrayList<>();
        for (Piece piece : pieces) {
            Piece newPiece = new Piece(piece);
            newPieces.add(newPiece);
            board.getTile(piece.getX(), piece.getY()).setPiece(newPiece);
        }
        return newPieces;
    }

    public void setPieces() {
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

    public void setCustomPieces() {
        playerRedPieces.removeIf(p -> p.getName().equals("R2"));
        playerRedPieces.removeIf(p -> p.getName().equals("R4"));
        playerRedPieces.removeIf(p -> p.getName().equals("R5"));
        playerBluePieces.removeIf(p -> p.getName().equals("B6"));
        board.getTile(0, 0).setPiece(playerRedPieces.stream().filter(p -> p.getName().equals("R1")).findFirst().get());
        board.getTile(0, 1).setPiece(playerRedPieces.stream().filter(p -> p.getName().equals("R6")).findFirst().get());
        board.getTile(0, 2).setPiece(playerRedPieces.stream().filter(p -> p.getName().equals("R0")).findFirst().get());
        board.getTile(3, 3).setPiece(playerRedPieces.stream().filter(p -> p.getName().equals("RM")).findFirst().get());
        board.getTile(4, 0).setPiece(playerBluePieces.stream().filter(p -> p.getName().equals("B5")).findFirst().get());
        board.getTile(6, 1).setPiece(playerBluePieces.stream().filter(p -> p.getName().equals("BM")).findFirst().get());
        board.getTile(5, 4).setPiece(playerBluePieces.stream().filter(p -> p.getName().equals("B1")).findFirst().get());
        board.getTile(1, 6).setPiece(playerBluePieces.stream().filter(p -> p.getName().equals("B2")).findFirst().get());
        board.getTile(4, 6).setPiece(playerBluePieces.stream().filter(p -> p.getName().equals("B4")).findFirst().get());
        board.getTile(6, 6).setPiece(playerBluePieces.stream().filter(p -> p.getName().equals("B0")).findFirst().get());
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

    public boolean isGameWon() {
        return gameState == GameState.FINISHED;
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

    public void setWinTroughTemple(boolean winTroughTemple) {
        this.winTroughTemple = winTroughTemple;
    }

    @JsonProperty("gameId")
    public String getGameId() {
        return gameId;
    }

    @JsonProperty("beginTime")
    public long getBeginTime() {
        return beginTime;
    }

    @JsonProperty("turnStartTime")
    public long getTurnStartTime() {
        return turnStartTime;
    }

    @JsonProperty("gameState")
    public GameState getGameState() {
        return gameState;
    }

    @JsonProperty("board")
    public Board getBoard() {
        return board;
    }

    @JsonProperty("playerRed")
    public Player getPlayerRed() {
        return playerRed;
    }

    @JsonProperty("playerBlue")
    public Player getPlayerBlue() {
        return playerBlue;
    }

    @JsonProperty("currentPlayer")
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    @JsonProperty("playerRedCards")
    public ArrayList<Card> getPlayerRedCards() {
        return playerRedCards;
    }

    @JsonProperty("playerBlueCards")
    public ArrayList<Card> getPlayerBlueCards() {
        return playerBlueCards;
    }

    @JsonProperty("playerRedPieces")
    public List<Piece> getPlayerRedPieces() {
        return playerRedPieces;
    }

    @JsonProperty("playerBluePieces")
    public List<Piece> getPlayerBluePieces() {
        return playerBluePieces;
    }

    @JsonProperty("nextCard")
    public Card getNextCard() {
        return nextCard;
    }

    @JsonProperty("winTroughTemple")
    public boolean isWinTroughTemple() {
        return winTroughTemple;
    }
}
