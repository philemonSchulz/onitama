package com.example.aimodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.controller.MoveController;
import com.example.helperObjects.SimulationResult;
import com.example.model.Game;
import com.example.model.Move;
import com.example.model.Player;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;

public class MCTSRaveTest {

    private static final double CVALUE = 0.3;
    private double RAVE_BIAS = 15;
    private Player currentPlayer;
    private double bias;
    private double biasCOunter;

    public Move raveUctSearch(Game game, boolean useTimeInsteadOfIterations, double raveBias) {
        Node rootNode = new Node(new MctsState(game), null, null);
        this.RAVE_BIAS = raveBias;

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

        currentPlayer = game.getCurrentPlayer();
        if (false) {
            System.out.println("Iterations: " + iterations);
            System.out.println(rootNode.getVisits());
            game.getBoard().printBoard();
            System.out.println(RAVE_BIAS);

        }

        Move bestMove = bestChild(rootNode, 0).getIncomingMove();

        if (false) {
            System.out.println("Best move: " + bestMove.getPiece().getName() + " "
                    + bestMove.getMovement().getX(currentPlayer.getColor()) + " "
                    + bestMove.getMovement().getY(currentPlayer.getColor()));
        }

        return bestMove;
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
        boolean log = false;
        if (cValue == 0.0) {
            log = false;
            // System.out.println("Bias: " + bias / biasCOunter);
        }

        for (Node child : node.getChildren()) {
            double uctValue = (child.getValue() / child.getVisits())
                    + cValue * Math.sqrt(2 * Math.log(node.getVisits()) / child.getVisits());
            if (uctValue > bestUTC) {
                bestUTC = uctValue;
            }

            double raveBias = ((RAVE_BIAS - node.getVisits()) / RAVE_BIAS) > 0
                    ? ((RAVE_BIAS - node.getVisits()) / RAVE_BIAS)
                    : 0;
            bias += raveBias;
            biasCOunter++;

            if (child.getRaveWinRate() > bestRave) {
                bestRave = child.getRaveWinRate();
            }

            double raveValue = child.getRaveVisits() == 0 ? uctValue
                    : raveBias * child.getRaveWinRate() + (1 - raveBias) * uctValue;
            if (log) {
                ;
                System.out.println("Move: " + child.getIncomingMove().getPiece().getName() + " "
                        + child.getIncomingMove().getMovement().getX(currentPlayer.getColor()) + " "
                        + child.getIncomingMove().getMovement().getY(currentPlayer.getColor()) + "\t Reward: "
                        + child.getValue()
                        + "\t Visits: " + child.getVisits() + "\t Rave Visits: " + child.getRaveVisits()
                        + "\t Rave reward: " + child.getRaveReward() + "\t Winrate: "
                        + (child.getRaveWinRate()) + "\t Rave Value: " + raveValue
                        + "\t UCT: " + uctValue);
            }

            if (raveValue > bestValue) {
                bestValue = raveValue;
                bestChild = child;
            }
        }
        if (log) {
            System.out.println("Beste Value: " + bestValue + "\t Best UTC: " + bestUTC + "\t Best Rave: " + bestRave
                    + "\t Bias: " + bias / biasCOunter);
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