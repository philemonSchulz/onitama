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

public class MoveController {

    /**
     * A method that returns all possible moves for a player. List is ordered as
     * follows:
     * Winning moves > Moves that capture opponents pieces > Normal moves
     * 
     * @param game The current game
     * @return A list of MoveObjects containing all possible moves
     */

    public static LinkedList<Move> getAllPossibleMoves(Game game) {
        LinkedList<Move> normalMoves = new LinkedList<>();
        LinkedList<Move> winningMoves = new LinkedList<>();
        PlayerColor playerColor = game.getCurrentPlayer().getColor();
        boolean isPlayerRed = playerColor == PlayerColor.RED;

        for (Piece piece : isPlayerRed ? game.getPlayerRedPieces() : game.getPlayerBluePieces()) {
            for (Card card : isPlayerRed ? game.getPlayerRedCards() : game.getPlayerBlueCards()) {
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
                        // add it first to the normal moves list
                        else if (isOpponentsPiece) {
                            normalMoves.addFirst(new Move(move, piece, potentialOpponentsPiece, card));
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

        winningMoves.addAll(normalMoves);
        return winningMoves;
    }
}
