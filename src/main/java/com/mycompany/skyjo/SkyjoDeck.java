/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.skyjo;

/*
5 -2s
10 -1s
15 0s
10 1s 2s 3s 4s
10 5s 6s 7s 8s
10 9s 10s 11s 12s
-----------------
150 cards total

** Need to add logic for drawing the last discarded card to your board instead of from the deck **
** Need to add logic for placing a completed column + old card to the discard pile **
*/
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
public class SkyjoDeck {
    enum ColorCounts {
        Red()
    }
    private SkyjoCard[] cards;
    private int cardsInDeck;

    public SkyjoDeck() {
        cards = new SkyjoCard[150];
    }

    public void reset() {
        cardsInDeck = 0;
        createByColor(SkyjoCard.Color.Red);
        createByColor(SkyjoCard.Color.Yellow);
        createByColor(SkyjoCard.Color.Green);
        createByColor(SkyjoCard.Color.Teal);
        createByColor(SkyjoCard.Color.Blue);
    }

    private void createByColor(SkyjoCard.Color color) {
        int valuesPerColor = 0;
        int cardsPerValue = 0;
        int startingValue = 0;

        switch (color) {
            case Red:
                valuesPerColor = 4;
                cardsPerValue = 10;
                startingValue = 9;
                break;
            case Yellow:
                valuesPerColor = 4;
                cardsPerValue = 10;
                startingValue = 5;
                break;
            case Green:
                valuesPerColor = 4;
                cardsPerValue = 10;
                startingValue = 1;
                break;
            case Teal:
                valuesPerColor = 1;
                cardsPerValue = 15;
                startingValue = 0;
                break;
            case Blue:
                valuesPerColor = 2;
                cardsPerValue = 10;
                startingValue = -2;
                break;
        }

        for(int i = startingValue; i < (startingValue + valuesPerColor); i++) {
            for(int j = 0; j < cardsPerValue; j++) {
                // Work-around since there are only 5 -2s and 10 -1s
                if (color != SkyjoCard.Color.Blue || !((i==-2) && (j > 4))) {
                    cards[cardsInDeck++] = new SkyjoCard(i);
                } 
            }
        }
    }

    public void replaceDeckWith(ArrayList<SkyjoCard> cards) {
        this.cards = cards.toArray(new SkyjoCard[cards.size()]);
        this.cardsInDeck = this.cards.length;
    }

    public boolean isEmpty() {
        return cardsInDeck == 0;
    }

    public void shuffle() {
        int n = cards.length;
        Random random = new Random();

        for(int i = 0; i < cards.length; i++) {
            int randomValue = i + random.nextInt(n - i);
            SkyjoCard randomCard = cards[randomValue];
            cards[randomValue] = cards[i];
            cards[i] = randomCard;
        }

    }

    public SkyjoCard drawCard() throws IllegalArgumentException {
        if (isEmpty()) {
            throw new IllegalArgumentException("Cannot draw a card, since the deck is empty.");
        }
        return cards[--cardsInDeck];
    }

    public ImageIcon drawCardImage() throws IllegalArgumentException {
        if (isEmpty()) {
            throw new IllegalArgumentException("Cannot draw a card, since the deck is empty.");
        }
        return new ImageIcon(cards[--cardsInDeck].toString() + ".png");
    }
}

