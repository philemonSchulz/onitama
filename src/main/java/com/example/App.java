package com.example;

import com.example.aimodels.RandomAi;
import com.example.client.OnitamaClient;
import com.example.model.Card;
import com.example.model.Game;
import com.example.model.Game.GameState;
import com.example.model.Piece.PieceType;
import com.example.model.Move;
import com.example.model.Piece;
import com.example.model.Player;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;
import com.example.service.CardCreator;
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

        CardCreator cardCreator = new CardCreator();
        Card[] cards = cardCreator.getFiveRandomCards();
        Piece piece1 = new Piece(PieceType.MASTER, PlayerColor.RED, "master", 0, 0);
        Piece piece2 = new Piece(PieceType.MASTER, PlayerColor.RED, "master", 0, 0);
        Piece piece3 = new Piece(PieceType.STUDENT, PlayerColor.RED, "student", 4, 4);
        Card card = cards[0];
        Card card2 = new Card(card);
        Move move1 = new Move(card.getMoves().iterator().next(), piece1, null, card);
        Move move2 = new Move(card2.getMoves().iterator().next(), piece2, null, card2);
        System.out.println(move1.equals(move2));

        OnitamaClient client = new OnitamaClient();
        client.run();
    }
}
