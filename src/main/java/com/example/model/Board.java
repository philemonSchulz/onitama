package com.example.model;

import java.util.ArrayList;

import com.example.service.CardCreator;
import com.example.model.Piece.PieceType;
import com.example.model.Player.PlayerColor;

public class Board {
    public static final int BOARD_SIZE = 7;
    private Tile[][] board;
    private ArrayList<Card> playerRedCards;
    private ArrayList<Card> playerBlueCards;
    private ArrayList<Piece> playerRedPieces;
    private ArrayList<Piece> playerBluePieces;
    private Card nextCard;
    private boolean gameWon;

    public Board() {
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (x == 3 && y == 0) {
                    this.board[x][y] = new Tile(x, y, Player.PlayerColor.RED);
                } else if (x == 3 && y == BOARD_SIZE - 1) {
                    this.board[x][y] = new Tile(x, y, Player.PlayerColor.BLUE);
                } else {
                    this.board[x][y] = new Tile(x, y);
                }
            }
        }
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
        this.gameWon = false;
        setFigures();
    }

    public Board(Board board) {
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                this.board[x][y] = new Tile(board.getTile(x, y));
            }
        }
        this.playerRedCards = new ArrayList<>();
        for (Card card : board.getPlayerRedCards()) {
            this.playerRedCards.add(new Card(card));
        }
        this.playerBlueCards = new ArrayList<>();
        for (Card card : board.getPlayerBlueCards()) {
            this.playerBlueCards.add(new Card(card));
        }
        this.nextCard = new Card(board.getNextCard());
        this.playerRedPieces = new ArrayList<>();
        for (Piece piece : board.getPlayerRedPieces()) {
            this.playerRedPieces.add(new Piece(piece));
        }
        this.playerBluePieces = new ArrayList<>();
        for (Piece piece : board.getPlayerBluePieces()) {
            this.playerBluePieces.add(new Piece(piece));
        }
        this.gameWon = board.isGameWon();
    }

    public ArrayList<Piece> createFigures(PlayerColor playerColor) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i == (BOARD_SIZE / 2)) {
                pieces.add(
                        new Piece(Piece.PieceType.MASTER, playerColor, playerColor == PlayerColor.RED ? "RM" : "BM"));
            } else {
                pieces.add(new Piece(Piece.PieceType.STUDENT, playerColor, playerColor == PlayerColor.RED ? "R" : "B"));
            }
        }
        return pieces;
    }

    public void setFigures() {
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (y == 0) {
                    board[x][y].setPiece(playerRedPieces.get(x));
                } else if (y == BOARD_SIZE - 1) {
                    board[x][y].setPiece(playerBluePieces.get(BOARD_SIZE - 1 - x));
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

    public Tile getTile(int x, int y) {
        return board[x][y];
    }

    public ArrayList<Card> getPlayerRedCards() {
        return playerRedCards;
    }

    public ArrayList<Card> getPlayerBlueCards() {
        return playerBlueCards;
    }

    public Card getNextCard() {
        return nextCard;
    }

    public ArrayList<Piece> getPlayerRedPieces() {
        return playerRedPieces;
    }

    public ArrayList<Piece> getPlayerBluePieces() {
        return playerBluePieces;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void printBoard() {
        System.out.println("-----------------------------");
        for (int y = BOARD_SIZE - 1; y >= 0; y--) {
            System.out.print("|");
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (board[x][y].getPiece() != null) {
                    if (board[x][y].getPiece().getType() == PieceType.MASTER) {
                        System.out.print(board[x][y].getPiece().getName());
                    } else {
                        System.out.print(board[x][y].getPiece().getName());
                    }
                } else {
                    System.out.print("  ");
                }
                System.out.print("|");
            }
            System.out.println("");
            System.out.println("-----------------------------");
        }
    }
}
