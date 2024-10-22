package com.example.aimodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.aimodels.MCTS.MctsState;
import com.example.controller.MoveController;
import com.example.model.Card;
import com.example.model.Game;
import com.example.model.GameStats;
import com.example.model.Game.GameState;
import com.example.model.Player.PlayerColor;
import com.example.service.GameService;
import com.example.model.Move;
import com.example.model.Movement;
import com.example.model.Piece;
import com.example.model.Player;

public class MCTSRave {

    private static final double CVALUE = 0.7;
    private static final double RAVE_BIAS = 0.5;

    public Move raveUctSearch(Game game, boolean useTimeInsteadOfIterations) {
        Node rootNode = new Node(null, new RaveMctsState(game), null);

        int iterations = 0;
        long startTime = System.currentTimeMillis();
        long timeLimit = 3000;

        while (useTimeInsteadOfIterations ? System.currentTimeMillis() < startTime + timeLimit
                : iterations < 1000) {
            Node selectedNode = treePolicy(rootNode);
            SimulationResult result = defaultPolicy(selectedNode.getState());
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
                        + child.getReward()
                        + "\t Visits: " + child.getVisitCount() + "\t Winrate: "
                        + (child.getReward() / child.getVisitCount()));
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
        return new Node(node, node.getState().getNexMctsState(move), move);
    }

    private Node bestChild(Node node, double cValue) {
        Node bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Node child : node.getChildren()) {
            double ucbValue = (child.getReward() / child.getVisitCount())
                    + cValue * Math.sqrt(2 * Math.log(node.getVisitCount()) / child.getVisitCount());

            Move incommingMove = child.getIncomingMove();
            double amafWinRate = child.getAMAFWinRate(incommingMove);
            int amafVisitCount = child.getAmafVisits(incommingMove);

            double raveBias = (amafVisitCount > 0) ? (RAVE_BIAS * amafWinRate) + ((1 - RAVE_BIAS) * ucbValue)
                    : ucbValue;

            double uctRaveValue = raveBias
                    + cValue * Math.sqrt(2 * Math.log(node.getVisitCount()) / child.getVisitCount());

            if (uctRaveValue > bestValue) {
                bestValue = uctRaveValue;
                bestChild = child;
            }
        }

        return bestChild;
    }

    private SimulationResult defaultPolicy(RaveMctsState state) {
        return state.runSimulation();
    }

    private void backup(Node node, int reward, List<Move> playedMoves) {
        Node currentNode = node;
        while (currentNode != null) {
            currentNode.visitCount++;
            currentNode.reward += reward;

            for (Move move : playedMoves) {
                currentNode.updateAMAF(move, reward);
            }
            if (reward == 0) {
                reward = 1;
            } else {
                reward = 0;
            }
            currentNode = currentNode.getParent();
        }
    }

    public class Node {
        private RaveMctsState state;
        private Node parent;
        private List<Node> children;
        private Move incomingMove;
        private int visitCount;
        private double reward;

        private double raveScore;
        private int raveVisits;

        private Map<Move, Integer> amafVisits;
        private Map<Move, Double> amafWins;

        public Node(Node parent, RaveMctsState gameState, Move incomingMove) {
            this.state = gameState;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.visitCount = 0;
            this.reward = 0;
            this.raveScore = 0;
            this.raveVisits = 0;
            this.amafVisits = new HashMap<>();
            this.amafWins = new HashMap<>();
            this.incomingMove = incomingMove;
        }

        public void addChild(Node childNode) {
            this.children.add(childNode);
        }

        public void updateAMAF(Move move, double reward) {
            this.amafVisits.put(move, this.amafVisits.getOrDefault(move, 0) + 1);
            this.amafWins.put(move, this.amafWins.getOrDefault(move, 0.0) + reward);
        }

        public double getAMAFWinRate(Move move) {
            if (!amafVisits.containsKey(move))
                return 0;
            return amafWins.get(move) / amafVisits.get(move);
        }

        public int getAmafVisits(Move move) {
            return amafVisits.getOrDefault(move, 0);
        }

        public Node getParent() {
            return parent;
        }

        public List<Node> getChildren() {
            return children;
        }

        public RaveMctsState getState() {
            return state;
        }

        public Move getIncomingMove() {
            return incomingMove;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public double getReward() {
            return reward;
        }

        public double getRaveScore() {
            return raveScore;
        }

        public int getRaveVisits() {
            return raveVisits;
        }

        public Map<Move, Integer> getAmafVisits() {
            return amafVisits;
        }

        public Map<Move, Double> getAmafWins() {
            return amafWins;
        }
    }

    public class RaveMctsState {
        private Game game;
        private boolean isTerminalState;
        private List<Move> possibleMoves;
        private GameService gameService;
        private PlayerColor winner;

        public RaveMctsState(Game game) {
            this.game = game;
            this.isTerminalState = game.getGameState() == GameState.FINISHED;
            this.possibleMoves = MoveController.getAllPossibleMovesAsObject(game).getAllMoves();
            this.gameService = new GameService();
        }

        public SimulationResult runSimulation() {
            SimulationResult result = gameService.runRandomRaveGame(new Game(game), new ArrayList<>());
            return result;
        }

        public RaveMctsState getNexMctsState(Move move) {
            Game newGame = new Game(game);
            gameService.processMove(newGame, move);
            gameService.switchTurn(newGame);
            return new RaveMctsState(newGame);
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

    public class RaveMove {
        private Movement move;
        private Piece piece;
        private Card card;

        public RaveMove(Movement move, Piece piece, Card card) {
            this.move = move;
            this.piece = piece;
            this.card = card;
        }

        public Movement getMove() {
            return move;
        }

        public Piece getPiece() {
            return piece;
        }

        public Card getCard() {
            return card;
        }

    }
}
