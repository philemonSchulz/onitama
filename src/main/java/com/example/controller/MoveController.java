package com.example.controller;

import java.util.LinkedList;

import com.example.model.Card;
import com.example.model.Game;
import com.example.model.Movement;
import com.example.model.Move;
import com.example.model.Piece;
import com.example.model.Tile;
import com.example.model.Piece.PieceType;
import com.example.model.Player.PlayerColor;
import com.example.model.PossibleMovesObject;

public class MoveController {

    /**
     * A method that returns an Object containing all possible moves for a player.
     * Object contains three lists:
     * Winning moves, Moves that capture opponents pieces, Normal moves
     * 
     * @param game The current game
     * @return An object containing all possible moves for a player
     */

    public static PossibleMovesObject getAllPossibleMovesAsObject(Game game) {
        LinkedList<Move> normalMoves = new LinkedList<>();
        LinkedList<Move> capturingMoves = new LinkedList<>();
        LinkedList<Move> winningMoves = new LinkedList<>();
        PlayerColor playerColor = game.getCurrentPlayer().getColor();
        boolean isPlayerRed = playerColor == PlayerColor.RED;

        for (Piece piece : (isPlayerRed ? game.getPlayerRedPieces() : game.getPlayerBluePieces())) {
            for (Card card : (isPlayerRed ? game.getPlayerRedCards() : game.getPlayerBlueCards())) {
                if (card.getMoves().size() == 0) {
                    continue;
                }
                for (Movement move : card.getAllowedMoves(piece.getX(), piece.getY(), playerColor)) {
                    Tile targetTile = game.getBoard().getTile(piece.getX() + move.getX(playerColor),
                            piece.getY() + move.getY(playerColor));
                    Piece potentialOpponentsPiece = targetTile.getPiece();

                    // If your Master reaches the opponents temple
                    if (targetTile.isOpponentsTemple(playerColor) && piece.getType() == PieceType.MASTER
                            && (potentialOpponentsPiece == null || potentialOpponentsPiece.getColor() != playerColor)) {
                        winningMoves.add(new Move(move, piece, potentialOpponentsPiece, card));
                    }
                    // If the targetTile of the considerd move is occupied by a piece, check wether
                    // its your own or the opponents piece
                    else if (potentialOpponentsPiece != null) {
                        boolean isOpponentsPiece = potentialOpponentsPiece.getColor() != playerColor;

                        // If the targetTile of the considered move is occupied by the opponents Master,
                        // its a winning move
                        if (isOpponentsPiece && potentialOpponentsPiece.getType() == PieceType.MASTER) {
                            winningMoves.add(new Move(move, piece, potentialOpponentsPiece, card));
                        }
                        // If the targetTile of the considered move is occupied by opponents Student,
                        // add it to the capturing moves list
                        else if (isOpponentsPiece) {
                            capturingMoves.add(new Move(move, piece, potentialOpponentsPiece, card));
                        }
                        // If the targetTile of the considered move is occupied by your own piece, move
                        // isnt added to the lists as it is a invalid move
                    }
                    // If the targetTile of the considered move is empty, so its a normal, valid
                    // move
                    else {
                        normalMoves.add(new Move(move, piece, potentialOpponentsPiece, card));
                    }
                }
            }
        }

        return new PossibleMovesObject(winningMoves, capturingMoves, normalMoves);
    }
}
