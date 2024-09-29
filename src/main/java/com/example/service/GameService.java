package com.example.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.aimodels.RandomAi;
import com.example.exceptions.GameAlreadyStartedException;
import com.example.exceptions.GameNotFoundException;
import com.example.model.Game;
import com.example.model.MoveObject;
import com.example.model.Player;
import com.example.model.Game.GameState;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;

@Service
public class GameService {

    private HashMap<String, Game> games;
    private int gameIndex = 0;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public GameService() {
        games = new HashMap<>();
    }

    /*
     * Methods for Rest API from here:
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
            playAiMove(game);
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

    /*
     * Methods for Game Logic from here:
     */

    public boolean processMove(Game game, MoveObject move) {
        return true;
    }

    public void switchTurn(Game game) {
        game.setCurrentPlayer(game.getCurrentPlayer().getColor() == Player.PlayerColor.RED ? game.getPlayerBlue()
                : game.getPlayerRed());
        game.setTurnStartTime(System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/game-state/" + game.getGameId(), game);
    }

    public void playAiMove(Game game) {
        if (game.getGameState() == GameState.IN_PROGRESS && game.getPlayerBlue().isAi()) {
            MoveObject move = generateAiMove(game);
            processMove(game, move);
            switchTurn(game);
        }
    }

    private MoveObject generateAiMove(Game game) {
        switch (game.getPlayerBlue().getAiType()) {
            case RANDOM:
                return RandomAi.getMove(game, Player.PlayerColor.BLUE, false);
            case RANDOM_PRIOTIZING:
                return RandomAi.getMove(game, Player.PlayerColor.BLUE, true);
            case MCTS:
            default:
                return RandomAi.getMove(game, Player.PlayerColor.BLUE, false);
        }
    }

    public boolean isPlayerTurn(String gameId, PlayerColor playerColor) {
        Game game = games.get(gameId);
        return game.getCurrentPlayer().getColor() == playerColor;
    }

    public boolean hasTimeExpired(Game game) {
        long elapsedTime = System.currentTimeMillis() - game.getTurnStartTime();
        return elapsedTime > 30000;
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
