package com.example.server;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.exceptions.GameAlreadyStartedException;
import com.example.exceptions.GameNotFoundException;
import com.example.model.Game;
import com.example.model.Game.GameState;
import com.example.model.Move;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;
import com.example.service.ApiService;

@RestController
@RequestMapping("/game")
public class GameController {
    @Autowired
    private ApiService apiService;

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestParam(value = "aiType", required = false) AiType aiType) {
        if (aiType == null) {
            Game newGame = apiService.createClientGame();
            return ResponseEntity.ok(newGame);
        } else {
            Game newGame = apiService.createAiGame(aiType);
            return ResponseEntity.ok(newGame);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<Game> joinGame(@RequestParam("gameId") String gameId) {
        try {
            Game game = apiService.joinGame(gameId);
            return ResponseEntity.ok(game);
        } catch (GameNotFoundException | GameAlreadyStartedException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{gameId}/state")
    public ResponseEntity<Game> getGameState(@PathVariable("gameId") String gameId) {
        Game game = apiService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/{gameId}/isMyTurn/{playerColor}")
    public ResponseEntity<Boolean> isMyTurn(@PathVariable("gameId") String gameId,
            @PathVariable("playerColor") String playerColor) {
        Game game = apiService.getGameById(gameId);
        if (game != null) {
            if (game.getGameState() == GameState.WAITING_FOR_PLAYERS) {
                return ResponseEntity.ok(false);
            }
            return ResponseEntity.ok(apiService.isPlayerTurn(gameId, PlayerColor.valueOf(playerColor)));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{gameId}/isFinished")
    public ResponseEntity<Boolean> isGameFinished(@PathVariable("gameId") String gameId) {
        Game game = apiService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game.getGameState() == GameState.FINISHED);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Game>> getGames() {
        Map<String, Game> games = apiService.getGames();
        return ResponseEntity.ok(games);
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<String> submitMove(@PathVariable("gameId") String gameId,
            @RequestBody Move move,
            @RequestParam("playerColor") PlayerColor playerColor) {

        return apiService.handleMove(gameId, move, playerColor);
    }
}
