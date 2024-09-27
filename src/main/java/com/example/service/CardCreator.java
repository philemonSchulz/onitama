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

    public static Card[] createCards() {
        Card[] cards = new Card[16];
        Card card = new Card("Hahn", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(-1, -1);
        card.addMove(1, 0);
        card.addMove(1, 1);
        cards[0] = card;

        card = new Card("Krabbe", PlayerColor.BLUE);
        card.addMove(-2, 0);
        card.addMove(0, 1);
        card.addMove(2, 0);
        cards[1] = card;

        card = new Card("Affe", PlayerColor.BLUE);
        card.addMove(-1, -1);
        card.addMove(-1, 1);
        card.addMove(1, -1);
        card.addMove(1, 1);
        cards[2] = card;

        card = new Card("Gottesanbeterin", PlayerColor.RED);
        card.addMove(-1, 1);
        card.addMove(1, 1);
        card.addMove(0, -1);
        cards[3] = card;

        card = new Card("Tiger", PlayerColor.BLUE);
        card.addMove(0, -1);
        card.addMove(0, 2);
        cards[4] = card;

        card = new Card("Frosch", PlayerColor.RED);
        card.addMove(-2, 0);
        card.addMove(-1, 1);
        card.addMove(1, -1);
        cards[5] = card;

        card = new Card("Wildschwein", PlayerColor.RED);
        card.addMove(1, 0);
        card.addMove(-1, 0);
        card.addMove(0, 1);
        cards[6] = card;

        card = new Card("Elefant", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(-1, 1);
        card.addMove(1, 0);
        card.addMove(1, 1);
        cards[7] = card;

        card = new Card("Pferd", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(0, -1);
        card.addMove(0, 1);
        cards[8] = card;

        card = new Card("Gans", PlayerColor.BLUE);
        card.addMove(-1, 0);
        card.addMove(-1, 1);
        card.addMove(1, 0);
        card.addMove(1, -1);
        cards[9] = card;

        card = new Card("Kranich", PlayerColor.BLUE);
        card.addMove(0, 1);
        card.addMove(-1, -1);
        card.addMove(1, -1);
        cards[10] = card;

        card = new Card("Drache", PlayerColor.RED);
        card.addMove(-2, 1);
        card.addMove(2, 1);
        card.addMove(-1, -1);
        card.addMove(1, -1);
        cards[11] = card;

        card = new Card("Hase", PlayerColor.BLUE);
        card.addMove(-1, -1);
        card.addMove(1, 1);
        card.addMove(2, 0);
        cards[12] = card;

        card = new Card("Aal", PlayerColor.BLUE);
        card.addMove(-1, 1);
        card.addMove(-1, -1);
        card.addMove(1, 0);
        cards[13] = card;

        card = new Card("Kobra", PlayerColor.RED);
        card.addMove(-1, 0);
        card.addMove(1, 1);
        card.addMove(1, -1);
        cards[14] = card;

        card = new Card("Ochse", PlayerColor.BLUE);
        card.addMove(0, 1);
        card.addMove(0, -1);
        card.addMove(1, 0);
        cards[15] = card;

        return cards;
    }

}
