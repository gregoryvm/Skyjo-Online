/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.skyjo;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;

public class Game {
    private int currentPlayer;
    private String[] playerIds;
    private int[] roundScores;
    private int[] gameScores;
    private int turnsRemaining;
    private SkyjoDeck deck;
    private ArrayList<SkyjoCard> discardPile;
    private ArrayList<SkyjoBoard> playerBoards;
    private boolean finalTurn;
    private int turnCount;
    private int roundCount;
    //private SkyjoCard currCard;

    public Game(String[] pids) {
        deck = new SkyjoDeck();
        deck.reset(); // potential fix?   
        deck.shuffle();
        //System.out.println(deck.drawCard());
        discardPile = new ArrayList<SkyjoCard>();

        playerIds = pids;
        currentPlayer = 0;
        setTurnCount(1);
        roundCount = 0;
        finalTurn = false;
        turnsRemaining = 99999; // No countdown on turns remaining until first player is out
        playerBoards = new ArrayList<SkyjoBoard>();
        roundScores = new int[pids.length];
        gameScores = new int[pids.length];
        for(int i = 0; i < pids.length; i++) {
            SkyjoBoard board = new SkyjoBoard(deck);
            playerBoards.add(board);
            roundScores[i] = 0;
            gameScores[i] = 0;
        }
    }

    public void start(Game game) {
        SkyjoCard card = deck.drawCard();

        discardPile.add(card);
        // currentPlayer = (currentPlayer + 1) % playerIds.length;

    }

    public SkyjoCard getTopCard(int value) {
        return new SkyjoCard(value);
    }

    public ImageIcon getTopCardImage(SkyjoCard card) {
        return new ImageIcon(card.getColor() + "_" + card.getValue());
    }

    public boolean isGameOver() {
        for (int i = 0; i < playerIds.length; i++) {
            if (gameScores[i] >= 100) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoundEnding() {
        if (finalTurn == false) {
            for (int i = 0; i < playerIds.length; i++) {
                SkyjoBoard currBoard = playerBoards.get(i);
                if(currBoard.isOut() == true) {
                    finalTurn = true;
                    turnsRemaining = playerIds.length - 1;
                    return true;
                }
            }
            return false;
        } else {
            return true;
        } 
    }

    public String getCurrentPlayer() {
        return this.playerIds[this.currentPlayer];
    }

    public String getNextPlayer() {
        int index = this.currentPlayer + 1;
        if (index > playerIds.length - 1) {
            index = 0;
        }
        return this.playerIds[index];
    }

    public String[] getPlayers() {
        return playerIds;
    }

    public SkyjoBoard getPlayerBoard(String pid) {
        int index = Arrays.asList(playerIds).indexOf(pid);
        return playerBoards.get(index);
    }

    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if(this.playerIds[this.currentPlayer] != pid) {
            throw new InvalidPlayerTurnException("it is not " + pid + "'s turn", pid);
        }
    }

    // Call this after a player's turn ends by flipping or swapping
    public void submitDraw(String pid) throws InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        if(deck.isEmpty()) {
            deck.replaceDeckWith(discardPile);
            deck.shuffle();
        }

        // Draw a card and place on discard pile, current player chooses what to do with this card (swap or flip)
        SkyjoCard card = deck.drawCard();
        System.out.println(card);
        card.revealCard();
        discardPile.add(card);
    }

    class InvalidPlayerTurnException extends Exception {
        String playerId;
        
        public InvalidPlayerTurnException(String message, String pid) {
            super(message);
            playerId = pid;
        }

        public String getPid() {
            return playerId;
        }
    }

    public void submitAction(String pid, int column, int row, boolean swap) throws InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        if(swap) {
            submitSwap(pid,column,row);
        } else {
            submitFlip(pid,column,row);
        }
        SkyjoBoard board = getPlayerBoard(pid);
        ArrayList<SkyjoCard> swappedCards = board.columnCleared();
            if(swappedCards.size() == 3) {
                discardPile.add(swappedCards.get(0));
                discardPile.add(swappedCards.get(1));
                discardPile.add(swappedCards.get(2));
            }
        finalTurn = isRoundEnding();
        System.out.println(getTurnCount() + " = " + (currentPlayer + 1));
        System.out.println("Turns remaining = " + turnsRemaining);
        if(turnsRemaining == 0) {
            // Sum total scores
            for (int i = 0; i < playerIds.length; i++) {
                gameScores[i] += roundScores[i];
                roundScores[i] = 0;
            }
            if(isGameOver()) {
            // Check who has the lowest score, declare who won.
            int index = 0;
            int lowestScore = gameScores[0];
            for(int i = 0; i < playerIds.length; i++) {
                if(gameScores[i] < lowestScore) {
                    lowestScore = gameScores[i];
                    index = i;
                }     
            }
            
            JLabel message = new JLabel(playerIds[index] + " has won the game!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null, message);
            } else {
                startNewRound();
            // Increment the round count, reset the boards while retaining overall score and resetting round score?
            // Set the player's turn to the player with the highest score
            }
        } else if(((getTurnCount() / (currentPlayer + 1)) == 1) && board.revealedCount() < 2) {
            // If it's a player's first turn, let them flip two cards
            
        } else {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
            turnsRemaining--;
            setTurnCount(getTurnCount() + 1);
        }
    }

    public void submitFlip(String pid, int column, int row) {
        SkyjoBoard board = getPlayerBoard(pid);
        board.flipCard(column, row);
    }

    public void submitSwap(String pid, int column, int row) {
        SkyjoCard card = discardPile.get(discardPile.size()-1);
        SkyjoBoard board = getPlayerBoard(pid);
        SkyjoCard swappedCard = board.swapCard(card, column, row);
        discardPile.add(swappedCard);

        
    }
    
    public SkyjoCard getDiscardTop(){
        return this.discardPile.get(discardPile.size()-1);
    }
    
    public int getTurnCount() {
        return this.turnCount;
    }
    
    public void setTurnCount(int value) {
        this.turnCount = value;
    }
    
    public int getCurrentPlayerVal() {
        return this.currentPlayer;
    }
    
    public void startNewRound() {
        int index;
        System.out.println("In startNewRound!");
        // If both players flipped 2 cards
        if((getTurnCount() / (currentPlayer + 1)) > 1) {
            // If it's the first round, turn order determined by sum of 2 revealed cards
            // (the player with the highest sum starts)
            SkyjoBoard highestBoard = playerBoards.get(0);
            index = 0;
            if(roundCount == 0) {
                for (int i = 0; i < playerIds.length; i++) {
                    SkyjoBoard currBoard = playerBoards.get(i);
                    if(currBoard.getRevealedScore() > highestBoard.getRevealedScore()) {
                        highestBoard = currBoard;
                        index = i;
                    }
                }
                currentPlayer = index;
            // Otherwise, the player with the highest overall score starts
            } else {
                int highestScore = gameScores[0];
                index = 0;
                for (int i = 0; i < playerIds.length; i++) {
                    if(gameScores[i] > highestScore) {
                        highestScore = gameScores[i];
                        index = i;
                    }
                    // Reset the player board for the new round
                    SkyjoBoard board = new SkyjoBoard(deck);
                    playerBoards.set(i, board);
                    roundScores[i] = 0;

                }
                deck.reset(); // potential fix?   
                deck.shuffle();
                discardPile = new ArrayList<SkyjoCard>();
                currentPlayer = index;
                SkyjoCard card = deck.drawCard();
                discardPile.add(card);
                JLabel message = new JLabel("Round # " + roundCount + 1 + " has begun!");
                message.setFont(new Font("Arial",Font.BOLD,48));
                JOptionPane.showMessageDialog(null, message);
            }        
        }
        roundCount += 1;
    }
    public int getRoundCount() {
        return this.roundCount;
    }
}

