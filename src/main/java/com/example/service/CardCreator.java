package com.example.service;

import java.util.HashSet;

import com.example.model.Card;
import com.example.model.Player.PlayerColor;

/**
 * Cards found here:
 * 
 * @see https://brettspielbox.de/onitama/
 */

public class CardCreator {

    public static Card[] getFiveRandomCards() {
        Card[] cards = createCards();
        Card[] randomCards = new Card[5];
        HashSet<Integer> usedIndices = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            int randomIndex = (int) (Math.random() * 16);
            while (usedIndices.contains(randomIndex)) {
                randomIndex = (int) (Math.random() * 16);
            }
            randomCards[i] = cards[randomIndex];
            usedIndices.add(randomIndex);
        }
        return randomCards;
    }

    public static Card[] createCardsBasedOnNames(String[] cardNames) {
        Card[] cards = createCards();
        Card[] selectedCards = new Card[5];
        for (int i = 0; i < 5; i++) {
            for (Card card : cards) {
                if (card.getName().equals(cardNames[i])) {
                    selectedCards[i] = card;
                    break;
                }
            }
        }
        return selectedCards;
    }

    public static Card[] createCards() {
        Card[] cards = new Card[16];
        Card card = new Card("Rooster", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(-1, -1);
        card.addMove(1, 0);
        card.addMove(1, 1);
        cards[0] = card;

        card = new Card("Crab", PlayerColor.BLUE);
        card.addMove(-2, 0);
        card.addMove(0, 1);
        card.addMove(2, 0);
        cards[1] = card;

        card = new Card("Monkey", PlayerColor.BLUE);
        card.addMove(-1, -1);
        card.addMove(-1, 1);
        card.addMove(1, -1);
        card.addMove(1, 1);
        cards[2] = card;

        card = new Card("Mantis", PlayerColor.RED);
        card.addMove(-1, 1);
        card.addMove(1, 1);
        card.addMove(0, -1);
        cards[3] = card;

        card = new Card("Tiger", PlayerColor.BLUE);
        card.addMove(0, -1);
        card.addMove(0, 2);
        cards[4] = card;

        card = new Card("Frog", PlayerColor.RED);
        card.addMove(-2, 0);
        card.addMove(-1, 1);
        card.addMove(1, -1);
        cards[5] = card;

        card = new Card("Boar", PlayerColor.RED);
        card.addMove(1, 0);
        card.addMove(-1, 0);
        card.addMove(0, 1);
        cards[6] = card;

        card = new Card("Elephant", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(-1, 1);
        card.addMove(1, 0);
        card.addMove(1, 1);
        cards[7] = card;

        card = new Card("Horse", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(0, -1);
        card.addMove(0, 1);
        cards[8] = card;

        card = new Card("Goose", PlayerColor.BLUE);
        card.addMove(-1, 0);
        card.addMove(-1, 1);
        card.addMove(1, 0);
        card.addMove(1, -1);
        cards[9] = card;

        card = new Card("Crane", PlayerColor.BLUE);
        card.addMove(0, 1);
        card.addMove(-1, -1);
        card.addMove(1, -1);
        cards[10] = card;

        card = new Card("Dragon", PlayerColor.RED);
        card.addMove(-2, 1);
        card.addMove(2, 1);
        card.addMove(-1, -1);
        card.addMove(1, -1);
        cards[11] = card;

        card = new Card("Rabbit", PlayerColor.BLUE);
        card.addMove(-1, -1);
        card.addMove(1, 1);
        card.addMove(2, 0);
        cards[12] = card;

        card = new Card("Eel", PlayerColor.BLUE);
        card.addMove(-1, 1);
        card.addMove(-1, -1);
        card.addMove(1, 0);
        cards[13] = card;

        card = new Card("Cobra", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(1, 1);
        card.addMove(1, -1);
        cards[14] = card;

        card = new Card("Ox", PlayerColor.BLUE);
        card.addMove(0, 1);
        card.addMove(0, -1);
        card.addMove(1, 0);
        cards[15] = card;

        return cards;
    }

}
