package com.example.aimodels;

import java.util.LinkedList;
import java.util.List;

import com.example.controller.MoveController;
import com.example.model.Game;
import com.example.model.GameStats;
import com.example.model.Move;
import com.example.model.Player;
import com.example.model.Game.GameState;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class MCTS {
    private double CVALUE = 0.5;
    private Player currentPlayer;

    public class Node {
        private Node parent;
        private List<Node> children;
        private MctsState state;
        private Move incomingMove;
        private double reward;
        private int visits;

        public Node(MctsState state, Move incomingMove) {
            this.children = new LinkedList<Node>();
            this.state = state;
            this.incomingMove = incomingMove;
            this.reward = 0;
            this.visits = 0;
        }

        public Node(Node parent, MctsState state, Move incomingMove) {
            this.parent = parent;
            this.children = new LinkedList<Node>();
            this.state = state;
            this.incomingMove = incomingMove;
            this.reward = 0;
            this.visits = 0;
        }

        public void addChild(Node childNode) {
            this.children.add(childNode);
        }

        public Node getParent() {
            return parent;
        }

        public List<Node> getChildren() {
            return children;
        }

        public MctsState getState() {
            return state;
        }

        public Move getIncomingMove() {
            return incomingMove;
        }

        public double getReward() {
            return reward;
        }

        public int getVisits() {
            return visits;
        }
    }

    public Move uctSearch(Game game, boolean useTimeInsteadOfIterations, double cValue) {
        this.CVALUE = cValue;
        this.currentPlayer = game.getCurrentPlayer();
        Node rootNode = new Node(new MctsState(new Game(game)), null);
        int iterations = 0;

        if (useTimeInsteadOfIterations) {
            long startTime = System.currentTimeMillis();
            long timeLimit = 2000;
            while (System.currentTimeMillis() - startTime < timeLimit) {
                Node node = treePolicy(rootNode);
                int reward = defaultPolicy(node.getState());
                backup(node, reward);
                iterations++;
            }
        } else {
            for (int i = 0; i < 1000; i++) {
                Node node = treePolicy(rootNode);
                int reward = defaultPolicy(node.getState());
                backup(node, reward);
            }
        }
        if (false) {
            System.out.println("Iterations: " + iterations);
            game.getBoard().printBoard();
            for (Node child : rootNode.getChildren()) {
                System.out.println("Move: " + child.getIncomingMove().getPiece().getName() + " "
                        + child.getIncomingMove().getMove().getX(currentPlayer.getColor()) + " "
                        + child.getIncomingMove().getMove().getY(currentPlayer.getColor()) + "\t Reward: "
                        + child.getReward()
                        + "\t Visits: " + child.getVisits() + "\t Winrate: " + (child.getReward() / child.getVisits()));
            }
        }
        // TODO: Nicht die beste quote, sondern node mit meisten Simulationen
        // zurückgeben
        return bestChild(rootNode, 0).getIncomingMove();
    }

    public Node treePolicy(Node node) {
        while (!node.getState().isTerminalState()) {
            if (!node.getState().isFullyExpanded()) {
                Node child = expand(node);
                node.addChild(child);
                return child;
            } else {
                node = bestChild(node, CVALUE);
            }
        }
        return node;
    }

    public Node expand(Node node) {
        Move move = node.getState().getUntriedMove();
        Node child = new Node(node, node.getState().getNextMctsState(move), move);
        return child;
    }

    public Node bestChild(Node node, double cValue) {
        Node bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Node child : node.getChildren()) {
            double value = (child.getReward() / child.getVisits())
                    + cValue * Math.sqrt(2 * Math.log(node.getVisits()) / child.getVisits());
            if (value > bestValue) {
                bestValue = value;
                bestChild = child;
            }
        }
        if (bestChild == null) {
            System.out.println("Best child is null");
        }
        return bestChild;
    }

    public int defaultPolicy(MctsState state) {
        return state.runSimulation();
    }

    public void backup(Node node, int reward) {
        while (node != null) {
            node.visits++;
            node.reward += reward;
            if (reward == 0) {
                reward = 1;
            } else {
                reward = 0;
            }
            node = node.getParent();
        }
    }

    public class MctsState {
        private Game game;
        private boolean isTerminalState;
        private List<Move> possibleMoves;
        private GameService gameService;
        private PlayerColor winner;

        public MctsState(Game game) {
            this.game = game;
            this.isTerminalState = game.isGameWon();
            this.possibleMoves = MoveController.getAllPossibleMovesAsObject(game).getAllMoves();
            this.gameService = new GameService();
        }

        public int runSimulation() {
            GameStats gameStats = gameService.runRandomGame(new Game(game));
            this.winner = gameStats.getWinner().getColor();
            return gameStats.getWinner().getColor() == game.getCurrentPlayer().getColor() ? 1 : 0;
        }

        public MctsState getNextMctsState(Move move) {
            Game newGame = new Game(game);
            if (move.getPiece() == null) {
                System.out.println("Piece is null");
                game.getBoard().printBoard();
                System.out.println("Move is null");
            }
            if (move.getPiece().getX() == null || move.getPiece().getY() == null) {
                System.out.println("Move is null");
                game.getBoard().printBoard();
            }
            int x = move.getPiece().getX() + move.getMove().getX(game.getCurrentPlayer().getColor());
            int y = move.getPiece().getY() + move.getMove().getY(game.getCurrentPlayer().getColor());
            if (x < 0 || x >= 7 || y < 0 || y >= 7) {
                System.out.println("Move is out of bounds");
                game.getBoard().printBoard();
            }
            gameService.processMove(newGame, move);
            gameService.switchTurn(newGame);
            return new MctsState(newGame);
        }

        public Move getUntriedMove() {
            return possibleMoves.size() > 0 ? possibleMoves.remove(0) : null;
        }

        public boolean isFullyExpanded() {
            return possibleMoves.size() == 0;
        }

        public Game getGame() {
            return game;
        }

        public boolean isTerminalState() {
            return isTerminalState;
        }

        public List<Move> getPossibleMoves() {
            return possibleMoves;
        }

        public GameService getGameService() {
            return gameService;
        }
    }

}
