package com.example.aimodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.controller.MoveController;
import com.example.model.Game;
import com.example.model.Move;
import com.example.model.Player;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class MCTSRave2 {

    private static final double CVALUE = 0.7;
    private static final double RAVE_BIAS = 0.5;

    public Move raveUctSearch(Game game, boolean useTimeInsteadOfIterations) {
        Node rootNode = new Node(new MctsState(game), null, null);

        int iterations = 0;
        long startTime = System.currentTimeMillis();
        long timeLimit = 3000;

        while (useTimeInsteadOfIterations ? System.currentTimeMillis() < startTime + timeLimit
                : iterations < 1000) {
            Node selectedNode = treePolicy(rootNode);
            SimulationResult result = defaultPolicy(selectedNode);
            backup(selectedNode, result.getReward(), result.getPlayedMoves());
            iterations++;
        }

        Player currentPlayer = game.getCurrentPlayer();
        if (true) {
            System.out.println("Iterations: " + iterations);
            game.getBoard().printBoard();
            for (Node child : rootNode.getChildren()) {
                System.out.println("Move: " + child.getIncomingMove().getPiece().getName() + " "
                        + child.getIncomingMove().getMovement().getX(currentPlayer.getColor()) + " "
                        + child.getIncomingMove().getMovement().getY(currentPlayer.getColor()) + "\t Reward: "
                        + child.getValue()
                        + "\t Visits: " + child.getVisits() + "\t Winrate: "
                        + (child.getValue() / child.getVisits()));
            }
        }
        return bestChild(rootNode, 0).incomingMove;
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
        return new Node(node.getState().getNexMctsState(move), move, node);
    }

    public Node bestChild(Node node, double cValue) {
        Node bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Node child : node.getChildren()) {
            double uctValue = (child.getValue() / child.getVisits())
                    + cValue * Math.sqrt(2 * Math.log(node.getVisits()) / child.getVisits());

            Move incomingMove = child.getIncomingMove();
            double raveWinRate = child.getRaveWinRate(incomingMove);
            int raveVisits = child.getRaveVisits(incomingMove);

            double raveBias = ((RAVE_BIAS - node.getVisits()) / RAVE_BIAS) > 0
                    ? ((RAVE_BIAS - node.getVisits()) / RAVE_BIAS)
                    : 0;

            double raveValue = raveVisits == 0 ? uctValue : raveBias * raveWinRate + (1 - raveBias) * uctValue;

            if (raveValue > bestValue) {
                bestValue = raveValue;
                bestChild = child;
            }
        }

        return bestChild;
    }

    public SimulationResult defaultPolicy(Node node) {
        return node.getState().runSimulation();
    }

    public void backup(Node node, int reward, List<Move> playedMoves) {
        Node currentNode = node;
        boolean flip = false;
        while (currentNode != null) {
            currentNode.visits++;
            currentNode.value += flip ? 1 - reward : reward;
            for (Node child : currentNode.getChildren()) {
                for (Move move : playedMoves) {
                    if (child.getIncomingMove().equals(move)) {
                        child.updateAMAF(move, reward);
                    }
                }
            }
            currentNode = currentNode.getParent();
            flip = !flip;
        }
    }

    /*
     * HERE GOES THE NODE CLASS
     * 
     * 
     * 
     * 
     */

    public class Node {
        MctsState state;
        Move incomingMove;
        Node parent;
        List<Node> children;
        int visits;
        double value;
        Map<Move, Integer> raveVisits;
        Map<Move, Double> raveValue;

        public Node(MctsState state, Move incomingMove, Node parent) {
            this.state = state;
            this.incomingMove = incomingMove;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.visits = 0;
            this.value = 0;
            this.raveVisits = new HashMap<>();
            this.raveValue = new HashMap<>();
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public void updateAMAF(Move move, int reward) {
            raveVisits.put(move, raveVisits.getOrDefault(move, 0) + 1);
            raveValue.put(move, raveValue.getOrDefault(move, 0.0) + reward);
        }

        public double getRaveWinRate(Move move) {
            if (!raveVisits.containsKey(move)) {
                return 0;
            }
            return raveValue.get(move) / raveVisits.get(move);
        }

        public int getRaveVisits(Move move) {
            return raveVisits.getOrDefault(move, 0);
        }

        public MctsState getState() {
            return state;
        }

        public Move getIncomingMove() {
            return incomingMove;
        }

        public Node getParent() {
            return parent;
        }

        public List<Node> getChildren() {
            return children;
        }

        public int getVisits() {
            return visits;
        }

        public double getValue() {
            return value;
        }
    }

    public class MctsState {
        private Game game;
        private boolean isTerminalState;
        private List<Move> legalMoves;
        private GameService gameService;
        private PlayerColor winnerColor;

        public MctsState(Game game) {
            this.game = game;
            this.isTerminalState = game.getGameState() == Game.GameState.FINISHED;
            this.legalMoves = MoveController.getAllPossibleMovesAsObject(game).getAllMoves();
            this.gameService = new GameService();
        }

        public SimulationResult runSimulation() {
            return gameService.runRandomRaveGame(new Game(game), new ArrayList<>());
        }

        public boolean isFullyExpanded() {
            return legalMoves.isEmpty();
        }

        public Move getUntriedMove() {
            return !legalMoves.isEmpty() ? legalMoves.remove(0) : null;
        }

        public MctsState getNexMctsState(Move move) {
            Game newGame = new Game(game);
            gameService.processMove(newGame, move);
            gameService.switchTurn(newGame);
            return new MctsState(newGame);
        }

        public boolean isTerminalState() {
            return isTerminalState;
        }

        public List<Move> getLegalMoves() {
            return legalMoves;
        }
    }
}
