package com.example.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.model.Game;
import com.example.model.Player.AiType;

public class ApiClient {
    private final RestTemplate restTemplate;
    private final String baserUrl = "http://localhost:8080/";

    public ApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public Game createGame(boolean isAiGame, AiType aiType) {
        String endpoint = isAiGame ? "/game/create?aiType=" + aiType : "/game/create";
        String url = baserUrl + endpoint;

        ResponseEntity<Game> response = restTemplate.postForEntity(url, null, Game.class);
        return response.getBody();
    }
}
