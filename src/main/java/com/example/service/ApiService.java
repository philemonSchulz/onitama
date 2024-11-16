package com.example.service;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.exceptions.GameAlreadyStartedException;
import com.example.exceptions.GameNotFoundException;
import com.example.model.Game;
import com.example.model.Game.GameState;
import com.example.model.Move;
import com.example.model.Player;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;

@Service
public class ApiService {

    private HashMap<String, Game> games;
    private int gameIndex = 0;
    private GameService gameService;

    public ApiService() {
        this.games = new HashMap<>();
        this.gameService = new GameService();
    }

    /*
     * Methods for the Rest API from here:
     */

    public Game createClientGame() {
        String gameId = "ClientGame" + gameIndex++;
        Game game = new Game(gameId);
        game.setPlayerRed(new Player(Player.PlayerColor.RED));
        game.setGameState(GameState.WAITING_FOR_PLAYERS);

        games.put(gameId, game);
        return game;
    }

    public Game createAiGame(AiType aiType) {
        String gameId = "AiGame" + gameIndex++;
        Game game = new Game(gameId);
        game.setPlayerRed(new Player(Player.PlayerColor.RED));
        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE, aiType));
        game.setBeginTime(System.currentTimeMillis());
        game.setGameState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        games.put(gameId, game);

        if (game.getCurrentPlayer().getColor() == PlayerColor.BLUE) {
            gameService.playAiMove(game);
        }

        return game;
    }

    public Game joinGame(String gameId) throws GameNotFoundException {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException("Didn't find game with id: " + gameId);
        }
        if (game.getGameState() != GameState.WAITING_FOR_PLAYERS) {
            throw new GameAlreadyStartedException("Game with id: " + gameId + " is not waiting for players");
        }

        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE));
        game.setBeginTime(System.currentTimeMillis());
        game.setGameState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        return game;
    }

    public boolean isPlayerTurn(String gameId, PlayerColor playerColor) {
        Game game = games.get(gameId);
        return game.getCurrentPlayer().getColor() == playerColor;
    }

    public ResponseEntity<String> handleMove(String gameId, Move move, PlayerColor playerColor) {
        Game game = games.get(gameId);

        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
        }

        if (game.getGameState() != GameState.IN_PROGRESS) {
            return ResponseEntity.badRequest().body("Invalid game state: " + game.getGameState());
        }

        if (game.getCurrentPlayer().getColor() != playerColor) {
            return ResponseEntity.badRequest().body("It's not your turn");
        }

        if (!gameService.processMove(game, move)) {
            return ResponseEntity.badRequest().body("Invalid move");
        }

        gameService.switchTurn(game);

        if (game.getGameState() == GameState.FINISHED) {
            return ResponseEntity.ok("Move accepted. Game finished.");
        }

        if (game.getCurrentPlayer().isAi()) {
            gameService.playAiMove(game);
        }

        return ResponseEntity.ok("Move accepted");
    }

    /*
     * Setter and Getter from here:
     */

    public Game getGameById(String gameId) {
        return games.get(gameId);
    }

    public HashMap<String, Game> getGames() {
        return games;
    }
}
