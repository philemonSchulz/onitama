package com.example.konradanpassungen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Game;
import com.example.model.Game.GameState;
import com.example.model.Player.PlayerColor;

@RestController
@RequestMapping("/konradGames")
public class KonradGameController {
    @Autowired
    private KonradApiService apiService;

    @PostMapping("/create")
    public ResponseEntity<String> createGameKonrad(@RequestParam("cards") String[] cards,
            @RequestParam("aiType") String aiType) {
        String gameId = apiService.createGame(cards, aiType);
        return ResponseEntity.ok(gameId);
    }

    @GetMapping(("/test"))
    public ResponseEntity<KonradResultObject> test() {
        return ResponseEntity.ok(new KonradResultObject("RED", 1000, true));
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

    @GetMapping("/{gameId}/getLatestMove")
    public ResponseEntity<KonradMoveObject> getLatestMove(@PathVariable("gameId") String gameId) {
        return apiService.getLatestMove(gameId);
    }

    @PostMapping("/{gameId}/submitMove")
    public ResponseEntity<String> submitMove(@PathVariable("gameId") String gameId, @RequestBody KonradMoveObject move,
            @RequestParam("playerColor") String playerColor) {

        return apiService.submitMove(gameId, move, PlayerColor.valueOf(playerColor));
    }

    @GetMapping("/{gameId}/isFinished")
    public ResponseEntity<Boolean> isGameFinished(@PathVariable("gameId") String gameId) {
        Game game = apiService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game.getGameState() == GameState.FINISHED);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{gameId}/getResult")
    public ResponseEntity<KonradResultObject> getResult(@PathVariable("gameId") String gameId) {
        Game game = apiService.getGameById(gameId);
        if (game != null && game.getGameState() == GameState.FINISHED) {
            KonradResultObject result = new KonradResultObject(game);
            System.out.println(game.isWinTroughTemple() + ", " + game.getGameId() + ", " + result.isTempleWin());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
}
