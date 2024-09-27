package com.example.model;

public class Game {
    public enum GameState {
        WAITING_FOR_PLAYERS, IN_PROGRESS, FINISHED
    }

    private String id;
    private long beginTime;
    private GameState state;
    private Board board;
    private Player playerRed;
    private Player playerBlue;
    private Player currentPlayer;

    public void setId(String id) {
        this.id = id;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public void setState(GameState state) {
        this.state = state;
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

    public String getId() {
        return id;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public GameState getState() {
        return state;
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
}
