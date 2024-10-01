package com.example.client;

import java.lang.reflect.Type;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.example.model.Game;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class WebSocketGameClient {

    private StompSession session;
    private PlayerColor playerColor;
    private GameService gameService;

    public WebSocketGameClient(PlayerColor playerColor, GameService gameService) {
        this.playerColor = playerColor;
        this.gameService = gameService;
    }

    public void connect(String gameId) {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        stompClient.setTaskScheduler(taskScheduler);

        stompClient.connectAsync("ws://localhost:8080/game-websocket", new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Game.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Game game = (Game) payload;
                handleGameUpdate(game);
            }

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                WebSocketGameClient.this.session = session;
                System.out.println("Connected to WebSocket server successfully.");

                // Subscribe to the correct topic for game updates
                session.subscribe("/topic/game-state/" + gameId, new StompSessionHandlerAdapter() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Game.class; // Ensure the correct type
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        Game game = (Game) payload;
                        handleGameUpdate(game);
                    }
                });
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("Error connecting to WebSocket server: " + exception.getMessage());
            }
        });
    }

    private void handleGameUpdate(Game game) {
        if (game.getCurrentPlayer().getColor() == playerColor) {
            // Make a move
            System.out.println("It's your turn! Make a move.");
            gameService.getMoveForServerGame(game);
        } else {
            // Wait for opponent to make a move
            System.out.println("Waiting for opponent to make a move...");
        }
    }
}
