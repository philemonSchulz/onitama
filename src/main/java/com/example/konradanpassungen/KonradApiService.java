package com.example.konradanpassungen;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.model.Card;
import com.example.model.Game;
import com.example.model.Move;
import com.example.model.Movement;
import com.example.model.Piece;
import com.example.model.Player;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;
import com.example.service.CardCreator;
import com.example.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KonradApiService {

    private HashMap<String, Game> games;
    private HashMap<String, Move> latestMoves;
    private int gameIndex = 0;
    private GameService gameService;

    public KonradApiService() {
        this.games = new HashMap<>();
        this.latestMoves = new HashMap<>();
        this.gameService = new GameService();
    }

    public String createGame(String[] cards) {
        String gameId = "Game" + gameIndex++;
        Game game = new Game(gameId);

        game.setPlayerRed(new Player(Player.PlayerColor.RED, AiType.MCTS));
        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE));

        Card[] selectedCards = CardCreator.createCardsBasedOnNames(cards);
        ArrayList<Card> playerBlueCards = new ArrayList<>();
        ArrayList<Card> playerRedCards = new ArrayList<>();
        playerBlueCards.add(selectedCards[0]);
        playerBlueCards.add(selectedCards[1]);
        playerRedCards.add(selectedCards[2]);
        playerRedCards.add(selectedCards[3]);

        game.setPlayerRedCards(playerRedCards);
        game.setPlayerBlueCards(playerBlueCards);
        game.setNextCard(selectedCards[4]);

        game.setCurrentPlayer(selectedCards[4].getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        game.setBeginTime(System.currentTimeMillis());

        games.put(gameId, game);
        game.setGameState(Game.GameState.IN_PROGRESS);

        System.out.println("current player: " + game.getCurrentPlayer().getColor());
        if (game.getCurrentPlayer().getColor() == Player.PlayerColor.RED) {
            Move move = gameService.playAiMove(game);
            this.latestMoves.put(gameId, move);
        }

        return gameId;
    }

    public boolean isPlayerTurn(String gameId, PlayerColor playerColor) {
        Game game = games.get(gameId);
        return game.getCurrentPlayer().getColor() == playerColor;
    }

    public ResponseEntity<KonradMoveObject> getLatestMove(String gameId) {
        Move move = this.latestMoves.get(gameId);
        if (move != null) {
            KonradMoveObject moveObject = new KonradMoveObject(move);
            return ResponseEntity.ok(moveObject);
        }

        return ResponseEntity.badRequest().body(null);
    }

    public ResponseEntity<String> submitMove(String gameId, KonradMoveObject move, PlayerColor playerColor) {
        Game game = games.get(gameId);
        if (game == null) {
            return ResponseEntity.status(404).body("Game not found");
        }

        if (game.getGameState() != Game.GameState.IN_PROGRESS) {
            return ResponseEntity.status(400).body("Game is not in progress");
        }

        if (!gameService.isPlayerTurn(game, playerColor)) {
            return ResponseEntity.status(400).body("Not your turn");
        }

        Piece piece = game.getBoard().getTile(move.getX(), move.getY()).getPiece();
        if (piece == null || piece.getColor() != playerColor) {
            return ResponseEntity.status(400).body("Invalid move");
        }

        Movement movement = new Movement(move.getMovementX(), move.getMovementY());

        Card card = game.getBlueCardByName(move.getCardName());
        if (card == null) {
            return ResponseEntity.status(400).body("Invalid card");
        }

        Move newMove = new Move(movement, piece, null, card);

        boolean isValidMove = gameService.processMove(game, newMove);
        if (!isValidMove) {
            return ResponseEntity.status(400).body("Invalid move");
        }

        gameService.switchTurn(game);
        this.latestMoves.put(gameId, newMove);

        if (game.getCurrentPlayer().isAi()) {
            Move aiMove = gameService.playAiMove(game);
            this.latestMoves.put(gameId, aiMove);
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
