package com.example.client;

import java.util.Scanner;

import com.example.model.Player.AiType;
import com.example.service.GameService;

public class OnitamaClient {
    private Scanner scanner;
    private GameService gameService;

    public OnitamaClient() {
        this.scanner = new Scanner(System.in);
        this.gameService = new GameService();
    }

    public void run() {
        while (true) {
            printMainMenu();
            int choice = getChoice();
            switch (choice) {
                case 1 -> playLocally();
                case 2 -> connectToServer();
                case 3 -> gameService.runCustomTests();
                case 4 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("Welcome to Onitama");
        System.out.println("1. Play Locally");
        System.out.println("2. Connect to Server");
        System.out.println("3. Custom Tests");
        System.out.println("4. Exit");
    }

    private int getChoice() {
        return scanner.nextInt();
    }

    private void playLocally() {
        System.out.println("1. Player vs AI");
        System.out.println("2. AI vs AI");
        System.out.println("3. Back to Main Menu");

        int choice = getChoice();
        switch (choice) {
            case 1 -> {
                AiType aiType = chooseAiType("Choose AI difficulty:");
                gameService.playerVsAi(aiType);
            }
            case 2 -> {
                AiType aiType1 = chooseAiType("Choose AI for Player 1:");
                AiType aiType2 = chooseAiType("Choose AI for Player 2:");
                gameService.aiVsAi(aiType1, aiType2);
            }
            case 3 -> System.out.println("Returning to main menu...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    public AiType chooseAiType(String headString) {
        System.out.println(headString);
        System.out.println("1. Random");
        System.out.println("2. Random Ai with priority to capturing moves");
        System.out.println("3. Heuristic Ai");
        System.out.println("4. MCTS");
        System.out.println("5. RAVE MCTS");
        System.out.println("6. Heuristic MCTS");

        int aiChoice = getChoice();
        return switch (aiChoice) {
            case 1 -> AiType.RANDOM;
            case 2 -> AiType.RANDOM_PRIOTIZING;
            case 3 -> AiType.HEURISTIC;
            case 4 -> AiType.MCTS;
            case 5 -> AiType.RAVE_MCTS;
            case 6 -> AiType.HEURISTIC_MCTS;
            default -> AiType.RANDOM;
        };
    }

    private void connectToServer() {
        // Logic for server play
        GameClient apiClient = new GameClient(gameService);
        apiClient.start();
    }
}
