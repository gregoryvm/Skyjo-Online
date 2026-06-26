/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.skyjo;

import java.util.ArrayList;

public class SkyjoBoard {
    private SkyjoCard[][] cardGrid;

    public SkyjoBoard(SkyjoDeck deck) {
        this.cardGrid = new SkyjoCard[3][4];
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                cardGrid[i][j] = deck.drawCard();
            }
        }
    }

    public boolean isOut() {
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (cardGrid[i][j].getRevealed() == false) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getScore() {
        int score;
        score = 0;
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                score += cardGrid[i][j].getValue();
            }
        }
        return score;
    }

    // This function takes a given card, adds it to the specified position on
    // the player board, then returns the card previously in that position. 
    public SkyjoCard swapCard(SkyjoCard card, int column, int row) {
        SkyjoCard toDiscard = cardGrid[row][column];
        toDiscard.revealCard();
        cardGrid[row][column] = card;
        return toDiscard;
    }

    public void flipCard(int column, int row) {
        cardGrid[row][column].revealCard();
    }

    // This function checks if any columns on the board feature all matching values,
    // if they do all match those cards are cleared from their board and one of the
    // matching cards is returned. 
    public ArrayList<SkyjoCard> columnCleared() {
        ArrayList<SkyjoCard> returnCards = new ArrayList<SkyjoCard>();
        boolean matching;
        for(int i = 0; i < 4; i++) {
            matching = true;
            int currValue = cardGrid[0][i].getValue();
            for(int j = 1; j < 3; j++) {
                if(cardGrid[j][i].getValue() != currValue || cardGrid[j][i].getRevealed() == false) {
                    matching = false;
                } 
                if(matching && j == 2) {
                    returnCards.add(cardGrid[0][i]);
                    returnCards.add(cardGrid[1][i]);
                    returnCards.add(cardGrid[2][i]);
                    cardGrid[0][i].setIsCleared(true);
                    cardGrid[1][i].setIsCleared(true);
                    cardGrid[2][i].setIsCleared(true);
                }
            }
        }
        return returnCards;
    }
    
    public SkyjoCard[][] getGrid(){
        return this.cardGrid;
    }
}
