package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.exceptions.GameAlreadyStartedException;
import com.example.exceptions.GameNotFoundException;
import com.example.service.GameService;
import com.example.model.Game;
import com.example.model.MoveObject;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestParam(required = false) AiType aiType) {
        if (aiType == null) {
            Game newGame = gameService.createClientGame();
            return ResponseEntity.ok(newGame);
        } else {
            Game newGame = gameService.createAiGame(aiType);
            return ResponseEntity.ok(newGame);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<Game> joinGame(@RequestParam String gameId) {
        try {
            Game game = gameService.joinGame(gameId);
            return ResponseEntity.ok(game);
        } catch (GameNotFoundException | GameAlreadyStartedException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{gameId}/state")
    public ResponseEntity<Game> getGameState(@RequestParam String gameId) {
        Game game = gameService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Game>> getGames() {
        Map<String, Game> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<String> submitMove(@PathVariable String gameId, @RequestParam MoveObject move,
            @RequestParam String playerColor) {
        Game game = gameService.getGameById(gameId);

        if (game == null) {
            return ResponseEntity.status(404).body("Game not found.");
        }

        if (!gameService.isPlayerTurn(gameId, PlayerColor.valueOf(playerColor))) {
            return ResponseEntity.status(403).body("It's not your turn.");
        }

        if (gameService.hasTimeExpired(game)) {
            return ResponseEntity.status(408).body("Time has expired.");
        }

        boolean isValidMove = gameService.processMove(game, move);
        if (!isValidMove) {
            return ResponseEntity.badRequest().body("Move not valid.");
        }

        gameService.switchTurn(game);

        if (game.getCurrentPlayer().isAi()) {
            gameService.playAiMove(game);
        }

        return ResponseEntity.ok("Move accepted.");
    }
}
