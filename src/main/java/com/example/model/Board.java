package com.example.model;

import com.example.model.Piece.PieceType;

public class Board {
    public static final int BOARD_SIZE = 7;
    private Tile[][] board;

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
    }

    public Board(Board board) {
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                this.board[x][y] = new Tile(board.getTile(x, y));
            }
        }
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Tile getTile(int x, int y) {
        return board[x][y];
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

    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------\n");
        for (int y = BOARD_SIZE - 1; y >= 0; y--) {
            sb.append("|");
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (board[x][y].getPiece() != null) {
                    if (board[x][y].getPiece().getType() == PieceType.MASTER) {
                        sb.append(board[x][y].getPiece().getName());
                    } else {
                        sb.append(board[x][y].getPiece().getName());
                    }
                } else {
                    sb.append("  ");
                }
                sb.append("|");
            }
            sb.append("\n");
            sb.append("-----------------------------\n");
        }
        return sb.toString();
    }
}
