package com.example.aimodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.controller.MoveController;
import com.example.helperObjects.SimulationResult;
import com.example.model.Game;
import com.example.model.MCTSMoveObject;
import com.example.model.Move;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class MCTSRave {

    private static final double CVALUE = 0.3;
    private double RAVE_BIAS = 1700;

    public MCTSMoveObject raveUctSearch(Game game, boolean useTimeInsteadOfIterations) {
        Node rootNode = new Node(new MctsState(game), null, null);

        int iterations = 0;
        long startTime = System.currentTimeMillis();
        long timeLimit = 2000;

        while (useTimeInsteadOfIterations ? System.currentTimeMillis() < startTime + timeLimit
                : iterations < 1000) {
            Node selectedNode = treePolicy(rootNode);
            SimulationResult result = defaultPolicy(selectedNode);
            backup(selectedNode, result.getReward(), result.getPlayedMoves());
            iterations++;
        }

        Move bestMove = bestChild(rootNode, 0).getIncomingMove();

        return new MCTSMoveObject(bestMove, iterations);
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
        double bestUTC = Double.NEGATIVE_INFINITY;
        double bestRave = Double.NEGATIVE_INFINITY;

        for (Node child : node.getChildren()) {
            double uctValue = (child.getValue() / child.getVisits())
                    + cValue * Math.sqrt(2 * Math.log(node.getVisits()) / child.getVisits());
            if (uctValue > bestUTC) {
                bestUTC = uctValue;
            }

            double raveBias = ((RAVE_BIAS - node.getVisits()) / RAVE_BIAS) > 0
                    ? ((RAVE_BIAS - node.getVisits()) / RAVE_BIAS)
                    : 0;

            if (child.getRaveWinRate() > bestRave) {
                bestRave = child.getRaveWinRate();
            }

            double raveValue = child.getRaveVisits() == 0 ? uctValue
                    : raveBias * child.getRaveWinRate() + (1 - raveBias) * uctValue;

            if (raveValue > 0.99) {
                return child;
            }

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
            currentNode.value += (!flip ? 1 - reward : reward);
            for (Node child : currentNode.getChildren()) {
                for (Move move : playedMoves) {
                    if (child.getIncomingMove().equals(move)) {
                        child.updateRave(flip ? 1 - reward : reward);
                    }
                }
            }
            playedMoves.add(currentNode.getIncomingMove());
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
        double raveVist;
        double raveReward;

        public Node(MctsState state, Move incomingMove, Node parent) {
            this.state = state;
            this.incomingMove = incomingMove;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.visits = 0;
            this.value = 0;
            this.raveVist = 0;
            this.raveReward = 0;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public void updateRave(int reward) {
            raveVist++;
            raveReward += reward;
        }

        public double getRaveWinRate() {
            if (raveVist == 0) {
                System.out.println("Rave visits are 0");
                return 0;
            }
            return raveReward / raveVist;
        }

        public double getRaveVisits() {
            return raveVist;
        }

        public double getRaveReward() {
            return raveReward;
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
