package com.example.client;

import java.util.Map;
import java.util.Scanner;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.example.model.Game;
import com.example.model.MoveObject;
import com.example.model.Player.AiType;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class GameClient {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/game";
    private String gameId;
    private PlayerColor playerColor;
    private GameService gameService;
    private boolean gameIsRunning;

    public GameClient(GameService gameService) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        this.gameService = gameService;
        this.gameIsRunning = false;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean continueInServerMenu = true;

        while (continueInServerMenu) {
            System.out.println("\nServer Play Menu");
            System.out.println("1. List open games");
            System.out.println("2. Create game");
            System.out.println("3. Join game");
            System.out.println("4. Back to main menu");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    listOpenGames(); // List available games to join
                    break;
                case 2:
                    createGameOnServer(); // Create a new game on the server
                    break;
                case 3:
                    System.out.print("Enter Game ID to join: ");
                    String gameId = scanner.next();
                    joinGame(gameId); // Join an existing game
                    break;
                case 4:
                    continueInServerMenu = false; // Return to the main menu
                    break;
                default:
                    System.out.println("Invalid option, please choose again.");
            }
            while (gameIsRunning) {
                try {
                    if (isGameFinished()) {
                        gameIsRunning = false;
                        break;
                    }
                    if (checkIfMyTurn()) {
                        MoveObject move = gameService.getMoveForServerGame(getGameState());
                        if (move != null) {
                            submitMove(move);
                        }
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void listOpenGames() {
        String url = baseUrl + "/list";
        ResponseEntity<Map<String, Game>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Game>>() {
                });

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Game> openGames = response.getBody();
            if (openGames.isEmpty()) {
                System.out.println("No open games available.");
            } else {
                System.out.println("Open Games:");
                openGames.forEach((id, game) -> System.out.println(
                        "Game ID: " + id));
            }
        } else {
            System.out.println("Failed to fetch open games.");
        }
    }

    public void createGameOnServer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to play against AI or another player?");
        System.out.println("1. Against AI\n2. Against another player");
        int choice = scanner.nextInt();

        while (choice < 1 || choice > 2) {
            System.out.println("Invalid choice. Please try again.");
            choice = scanner.nextInt();
        }

        if (choice == 1) {
            System.out.println("Choose Ai Type:");
            System.out.println("1. Random");
            System.out.println("2. Random Ai with priority to capturing moves");
            System.out.println("3. MCTS");
            choice = scanner.nextInt();

            while (choice < 1 || choice > 3) {
                System.out.println("Invalid choice. Please try again.");
                choice = scanner.nextInt();
            }

            AiType aiType = null;
            switch (choice) {
                case 1:
                    aiType = AiType.RANDOM;
                    break;
                case 2:
                    aiType = AiType.RANDOM_PRIOTIZING;
                    break;
                case 3:
                    aiType = AiType.MCTS;
                    break;
            }

            createGame(true, aiType);
        } else if (choice == 2) {
            createGame(false, null);
        }

        this.gameIsRunning = true;
        checkIfMyTurn();
    }

    public void createGame(boolean isAiGame, AiType aiType) {
        String endpoint = isAiGame ? "/create?aiType=" + aiType : "/create";
        String url = baseUrl + endpoint;

        Game newGame = restTemplate.postForObject(url, null, Game.class);
        if (newGame != null) {
            this.gameId = newGame.getGameId();
            this.playerColor = PlayerColor.RED;
            System.out.println("Game created with ID: " + gameId);
        } else {
            System.out.println("Failed to create game.");
        }
    }

    public void joinGame(String gameId) {
        String url = baseUrl + "/join?gameId=" + gameId;

        Game game = restTemplate.postForObject(url, null, Game.class);
        if (game != null) {
            this.gameId = gameId;
            this.playerColor = PlayerColor.BLUE;
            System.out.println("Joined game with ID: " + gameId);

            this.gameIsRunning = true;
            checkIfMyTurn();
        } else {
            System.out.println("Failed to join game.");
        }
    }

    public boolean submitMove(MoveObject move) {
        String url = baseUrl + "/" + gameId + "/move?move=" + move + "&playerColor=" + playerColor;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MoveObject> request = new HttpEntity<>(move, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String responseBody = response.getBody();
            if ("Move accepted.".equals(responseBody)) {
                System.out.println("Move submitted.");
                return true;
            } else {
                System.out.println("Failed to submit move: " + responseBody);
                return false;
            }
        } else {
            String responseBody = response.getBody();
            if (response.getStatusCode().is4xxClientError()) {
                System.out.println("Client error: " + responseBody);
            } else if (response.getStatusCode().is5xxServerError()) {
                System.out.println("Server error: " + responseBody);
            } else {
                System.out.println("Failed to submit move: " + responseBody);
            }
            return false;
        }
    }

    public boolean isGameFinished() {
        String url = baseUrl + "/" + gameId + "/isFinished";

        ResponseEntity<Boolean> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Boolean.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            boolean isFinished = response.getBody();
            if (isFinished) {
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println("Failed to check if game is finished.");
            return false;
        }
    }

    public boolean checkIfMyTurn() {
        System.out.println(gameId);
        String url = baseUrl + "/" + gameId + "/isMyTurn/" + playerColor;

        ResponseEntity<Boolean> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Boolean.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            boolean isMyTurn = response.getBody();
            if (isMyTurn) {
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println("Failed to check if it's your turn.");
            return false;
        }
    }

    public Game getGameState() {
        String url = baseUrl + "/" + gameId + "/state";

        Game game = restTemplate.getForObject(url, Game.class);
        if (game != null) {
            return game;
        } else {
            System.out.println("Failed to get game state.");
            return null;
        }
    }
}
