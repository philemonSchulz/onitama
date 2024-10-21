package com.example;

import com.example.aimodels.RandomAi;
import com.example.client.OnitamaClient;
import com.example.model.Game;
import com.example.model.Game.GameState;
import com.example.model.Move;
import com.example.model.Player;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        if (false) {
            Game game = new Game("testgame");
            game.setPlayerRed(new Player(PlayerColor.RED, null));
            game.setPlayerBlue(new Player(PlayerColor.BLUE, AiType.RANDOM));
            game.setBeginTime(System.currentTimeMillis());
            game.setGameState(GameState.IN_PROGRESS);
            game.setCurrentPlayer(game.getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                    : game.getPlayerBlue());

            Game game2 = new Game(game);

            GameService gameService = new GameService();
            gameService.runRandomGame(game2);
        }
        OnitamaClient client = new OnitamaClient();
        client.run();
    }
}
