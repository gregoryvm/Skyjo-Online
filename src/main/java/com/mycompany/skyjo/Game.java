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
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class Game {
    private int currentPlayer;
    private String[] playerIds;
    private int[][] roundScores;
    private int[] gameScores;
    private int turnsRemaining;
    private SkyjoDeck deck;
    private ArrayList<SkyjoCard> discardPile;
    private ArrayList<SkyjoBoard> playerBoards;
    private boolean finalTurn;
    private int turnCount;
    private int roundCount;
    private int outIndex;
    private GameStage stage;
    
    public Game(String[] pids, GameStage gameStage) {
        stage = gameStage;
        deck = new SkyjoDeck();
        deck.reset();
        deck.shuffle();
        discardPile = new ArrayList<SkyjoCard>();

        playerIds = pids;
        currentPlayer = 0;
        setTurnCount(1);
        roundCount = 0;
        finalTurn = false;
        turnsRemaining = 99999; // No countdown on turns remaining until first player is out
        playerBoards = new ArrayList<SkyjoBoard>();
        roundScores = new int[pids.length][99];
        gameScores = new int[pids.length];
        for(int i = 0; i < pids.length; i++) {
            SkyjoBoard board = new SkyjoBoard(deck);
            playerBoards.add(board);
            roundScores[i][roundCount] = 0;
            gameScores[i] = 0;
        }
    }

    public void start(Game game) {
        SkyjoCard card = deck.drawCard();
        discardPile.add(card);
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
                    outIndex = getCurrentPlayerVal();
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

    public void submitDraw(String pid) throws InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        if(deck.isEmpty()) {
            deck.replaceDeckWith(discardPile);
            deck.shuffle();
        }

        // Draw a card and place on discard pile, current player chooses what to do with this card (swap or flip)
        SkyjoCard card = deck.drawCard();
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
                for(int i = 0; i < 3; i++) {
                    discardPile.add(new SkyjoCard(swappedCards.get(i)));
                } 
            }
    }
    
    public void incrementTurn(String pid) {
        SkyjoBoard board = getPlayerBoard(pid);
        finalTurn = isRoundEnding();
        
        if(turnsRemaining == 0) {      
            // Sum total scores
            int lowestIndex = 0;
            int lowestScore = 9999;
            for(int i = 0; i < playerIds.length; i++) {
                String currPid = playerIds[i];
                SkyjoBoard currBoard = getPlayerBoard(currPid);
                gameScores[i] += currBoard.getScore();
                roundScores[i][roundCount-1] = currBoard.getScore();
                if(roundScores[i][roundCount-1] < lowestScore) {
                    lowestIndex = i;
                    lowestScore = roundScores[i][roundCount-1];
                }
            }
            // If the first player "out" doesn't have the lowest round score, double it.
            if(lowestIndex != outIndex) {
                gameScores[outIndex] += roundScores[outIndex][roundCount-1];
                roundScores[outIndex][roundCount-1] += roundScores[outIndex][roundCount-1];
                JLabel message = new JLabel(playerIds[outIndex] + "'s round score got doubled!");
                message.setFont(new Font("Arial",Font.BOLD,48));
                JOptionPane.showMessageDialog(null, message);
            }
            if(isGameOver()) {
                // If the game is over, display the scoreboard.
                ArrayList<String> pids = new ArrayList<>();
                for(int i = 0; i < playerIds.length; i++) {
                    pids.add(playerIds[i]);
                }
                Point location = stage.getLocation();
                Scoreboard scoreBoard = new Scoreboard(this, pids);
                scoreBoard.setLocation(location);
                scoreBoard.setVisible(true);
                stage.dispose();
            } else {
                // Increment the round count, reset the boards while retaining overall score and resetting round score
                setTurnCount(1);
                startNewRound();
            }
        } else if(((getTurnCount() / (currentPlayer + 1)) == 1) && board.revealedCount() < 2) {
            // If it's a player's first turn, let them flip two cards 
        } else {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
            turnsRemaining--;
            setTurnCount(getTurnCount() + 1);
            
            if(getTurnCount() == playerIds.length + 1) {
                startNewRound();
                beginTurn();
                return;
            }
            
            beginTurn();
            // Check if it's a CPU turn and play their action
            //if(!stage.bots.get(getCurrentPlayerVal())){
                //cpuTurn();
            //}
        }
    }

    public void submitFlip(String pid, int column, int row) {
        SkyjoBoard board = getPlayerBoard(pid);
        board.flipCard(column, row);
    }

    public void submitSwap(String pid, int column, int row) {
        SkyjoCard card = discardPile.remove(discardPile.size()-1);
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
        
        if(roundCount > 0 && getTurnCount() == 1) {
            deck.reset(); 
            deck.shuffle();
            discardPile = new ArrayList<SkyjoCard>();
            currentPlayer = 0;
            SkyjoCard card = deck.drawCard();
            discardPile.add(card);
            finalTurn = false;
            turnsRemaining = 99999; // No countdown on turns remaining until first player is out
            
            JLabel message = new JLabel("Round # " + (roundCount+1) + " has begun!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null, message);
            
            // Reset the player board for the new round
            for (int i = 0; i < playerIds.length; i++) {
                SkyjoBoard board = new SkyjoBoard(deck);
                playerBoards.set(i, board);
                //roundScores[i][roundCount] = 0;
            }
        }
        
        // If both players flipped 2 cards
        if((getTurnCount() / (currentPlayer + 1)) > 1) {
            System.out.println("EVERY PLAYER HAS PLAYED FIRST TURN, DETERMINE STARTING PLAYER");
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
                }
                currentPlayer = index;   
            } 
            System.out.println("starting player = " + playerIds[currentPlayer]);
            roundCount += 1;
        }
        
    }
    public int getRoundCount() {
        return this.roundCount;
    }
    
    public int getGameScore(int index) {
        return gameScores[index];
    }
    
    public int getPlayerIndex(String name) {
        return Arrays.asList(playerIds).indexOf(name);
    }
    
    public int getRoundScore(int pid, int round) {
        return roundScores[pid][round];
    }
    
    public void cpuTurn() {   
        System.out.println("In CPU Turn");
        System.out.println("turnCount = " + turnCount);
        System.out.println("CPU Turn #" + turnCount / playerIds.length);
        System.out.println("getCurrentPlayerVal() + 1 = " + getCurrentPlayerVal());
        SkyjoBoard board = playerBoards.get(getCurrentPlayerVal());
        Timer timer = new Timer(500, e -> {
        int drawVal = getDiscardTop().getValue();
        boolean action = false;
        // On first turn of round flip two random cards
        if(turnCount == getCurrentPlayerVal() + 1) {
            int card = (int)(1 + Math.random() * 11);
            int rowIndex = (card / 4);
            int colIndex = (card % 4);
            //int col = (int)(Math.random() * 3);
            //int row = (int)(Math.random() * 4);
            
            while(board.revealedCount() < 2) {
                while(board.getGrid()[rowIndex][colIndex].getRevealed() == true) {
                    System.out.println("col = " + colIndex + ", row =" + rowIndex);
                    card = (int)(1 + Math.random() * 11);
                    rowIndex = (card / 4);
                    colIndex = (card % 4);
                }
                stage.setSwapFlag(false);
                stage.cardAction(card);
                action = true;
            }
        } else {
            System.out.println("IN CPU COLUMN CLEAR");
            /* 1: Check if you can clear a column, and clear it
                - If you'd go out and... 
                    - it would make you double: dont clear and continue down hierarchy
                    - it wont make you double or your score would be sub 10: clear and go out
                    - If its -2, -1, 0 dont clear it and continue down hierarchy
            */
            int i = 0;
            while(i < 4 && !action) {
                System.out.println("i = " + i);
                System.out.println("drawVal = " + drawVal);
                System.out.println("[0][i]  = " + board.getGrid()[0][i].getValue());
                System.out.println("[0][1]  = " + board.getGrid()[1][i].getValue());
                System.out.println("[0][2]  = " + board.getGrid()[2][i].getValue());
                int currVal = board.getGrid()[0][i].getValue();
                if(drawVal == currVal) {
                    if(board.getGrid()[1][i].getValue() == currVal && board.getGrid()[1][i].getRevealed() && !board.getGrid()[1][i].getIsCleared() && currVal > 0) {
                        System.out.println("[0][i] = " + board.getGrid()[0][i].getValue());
                        System.out.println("[1][i] = " + board.getGrid()[1][i].getValue());
                        System.out.println("drawVal = " + drawVal);
                        
                        stage.setSwapFlag(true);
                        stage.cardAction(i + 8);
                        action = true;
                    } else if(board.getGrid()[2][i].getValue() == currVal && board.getGrid()[2][i].getRevealed() && !board.getGrid()[2][i].getIsCleared() && currVal > 0) {
                        System.out.println("[0][i] = " + board.getGrid()[0][i].getValue());
                        System.out.println("[2][i] = " + board.getGrid()[2][i].getValue());
                        System.out.println("drawVal = " + drawVal);
                        
                        stage.setSwapFlag(true);
                        stage.cardAction(i + 4);
                        action = true;
                    }
                } else {
                    currVal = board.getGrid()[1][i].getValue();
                    if(board.getGrid()[2][i].getValue() == currVal && board.getGrid()[2][i].getRevealed() && !board.getGrid()[2][i].getIsCleared() && drawVal == currVal && currVal > 0) {
                        System.out.println("[1][i] = " + board.getGrid()[1][i].getValue());
                        System.out.println("[2][i] = " + board.getGrid()[2][i].getValue());
                        System.out.println("drawVal = " + drawVal);
                        
                        stage.setSwapFlag(true);
                        stage.cardAction(i);
                        action = true;
                    }    
                }
                i++;
            }
            /* 2: Set up matching columns 
                - If its early in the round, match columns of any number
                - If its mid/late round and the number is high, continue down hierarchy
            */
            if(!action) {
                System.out.println("IN CPU COLUMN MATCH");
                int val1;
                int val2;
                i = 0;
                while(i < 4 && !action) {
                    System.out.println("i = " + i);
                    int currVal = board.getGrid()[0][i].getValue();
                    int currVal2 = board.getGrid()[1][i].getValue();
                    int currVal3 = board.getGrid()[2][i].getValue();
                    System.out.println("drawVal = " + drawVal);
                    System.out.println("currVal = " + currVal);
                    System.out.println("currVal2 = " + currVal2);
                    System.out.println("currVal3 = " + currVal3);
                    
                    if(drawVal == currVal && !board.getGrid()[0][i].getIsCleared() && board.getGrid()[0][i].getRevealed()) {
                        
                        // If the drawn card matches the first in a column, swap it with the highest value card in that column
                        // * Non-revealed cards are assumed to be value 6
                        if(board.getGrid()[1][i].getRevealed()){
                            val1 = board.getGrid()[1][i].getValue();
                        } else {
                            val1 = 13;
                        }
                            
                        if(board.getGrid()[2][i].getRevealed()){
                            val2 = board.getGrid()[2][i].getValue();
                        } else {
                            val2 = 13;
                        }
                        System.out.println("val1 = " + val1);
                        System.out.println("val2 = " + val2);
                        
                        // The nested if ensures the CPU doesnt just infinitely swap numbers less than 1
                        // with themselves. Also if theres already 2 matching numbers lower than the potential new match dont do it.
                        if(val1 > val2 && val1 > 0) {
                          if(val1 != drawVal && drawVal > 0 && !(val1 == val2 && val1 != 13 && val1 < drawVal)) { // the 3rd Or does not fix the problem 
                            System.out.println("swap row 2");
                            stage.setSwapFlag(true);
                            stage.cardAction(i + 4);
                            action = true;  
                          }  
                        } else if(val2 > val1 && val2 > 0) {
                            if(val2 != drawVal && drawVal > 0 && !(val1 == val2 && val1 != 13 && val1 < drawVal)) {
                                System.out.println("swap row 3");
                                stage.setSwapFlag(true);
                                stage.cardAction(i + 8);
                                action = true;  
                            }
                        // If the values are the same, greater than 0 but still less than the draw card match with the draw card    
                        } else if((val1 == val2 && val1 > 0 && drawVal < val1) && !(val1 < 1 || val2 < 1)) {
                            System.out.println("swap row 2");
                                stage.setSwapFlag(true);
                                stage.cardAction(i + 4);
                                action = true;  
                        }                      
                        
                    } else if(drawVal == currVal2 && !board.getGrid()[1][i].getIsCleared() && board.getGrid()[1][i].getRevealed()) {
                        // If the drawn card matches the second in a column, swap it with the highest value card in that column
                        // * Non-revealed cards are assumed to be value 6
                        if(board.getGrid()[0][i].getRevealed()){
                            val1 = board.getGrid()[0][i].getValue();
                        } else {
                            val1 = 13;
                        }
                            
                        if(board.getGrid()[2][i].getRevealed()){
                            val2 = board.getGrid()[2][i].getValue();
                        } else {
                            val2 = 13;
                        }
                        
                        System.out.println("val1 = " + val1);
                        System.out.println("val2 = " + val2);
                        
                        // The nested if ensures the CPU doesnt just infinitely swap numbers less than 1
                        // with themselves. Also if theres already 2 matching numbers lower than the potential new match dont do it.
                        if(val1 > val2 && val1 > 0) {
                          if(val1 != drawVal && drawVal > 0 && !(val1 == val2 && val1 != 13 && val1 < drawVal)) {
                            System.out.println("swap row 1");
                            stage.setSwapFlag(true);
                            stage.cardAction(i);  
                            action = true;  
                          }
                        } else if(val2 > val1 && val2 > 0) {
                            if(val2 != drawVal && drawVal > 0 && !(val1 == val2 && val1 != 13 && val1 < drawVal)) {
                                System.out.println("swap row 3");
                                stage.setSwapFlag(true);
                                stage.cardAction(i + 8);
                                action = true;  
                            }
                        // If the values are the same, greater than 0 but still less than the draw card match with the draw card    
                        } else if((val1 == val2 && val1 > 0 && drawVal < val1) && !(val1 < 1 || val2 < 1)) {
                            System.out.println("swap row 1");
                                stage.setSwapFlag(true);
                                stage.cardAction(i);
                                action = true;  
                        }                        
                    } else if(drawVal == currVal3 && !board.getGrid()[2][i].getIsCleared() && board.getGrid()[2][i].getRevealed()) {
                        // If the drawn card matches the third in a column, swap it with the highest value card in that column
                        // * Non-revealed cards are assumed to be value 6

                        if(board.getGrid()[0][i].getRevealed()){
                            val1 = board.getGrid()[0][i].getValue();
                        } else {
                            val1 = 13;
                        }
                            
                        if(board.getGrid()[1][i].getRevealed()){
                            val2 = board.getGrid()[1][i].getValue();
                        } else {
                            val2 = 13;
                        }
                        
                        System.out.println("val1 = " + val1);
                        System.out.println("val2 = " + val2);
                        
                        
                        // The nested if ensures the CPU doesnt just infinitely swap numbers less than 1
                        // with themselves. Also if theres already 2 matching numbers lower than the potential new match dont do it.
                        if(val1 > val2 && val1 > 0) {
                            if(val1 != drawVal && drawVal > 0 && !(val1 == val2 && val1 != 13 && val1 < drawVal)) {
                                System.out.println("swap row 1");
                                stage.setSwapFlag(true);
                                stage.cardAction(i);  
                                action = true;  
                            }
                        } else if(val2 > val1 && val2 > 0) {
                          if(val2 != drawVal && drawVal > 0 && !(val1 == val2 && val1 != 13 && val1 < drawVal)) {
                                System.out.println("swap row 2");
                                stage.setSwapFlag(true);
                                stage.cardAction(i + 4);
                                action = true;  
                          }
                        // If the values are the same, greater than 0 but still less than the draw card match with the draw card
                        } else if((val1 == val2 && val1 > 0 && drawVal < val1) && !(val1 < 1 || val2 < 1)) {
                            System.out.println("swap row 1");
                                stage.setSwapFlag(true);
                                stage.cardAction(i);
                                action = true;  
                        }        
                    }
                    i++;
                }    
            }
            /* 3: Score reducing 
                - -2,-1 and 0 get put into the same column
                    - if theres one spot in these add non-clearing -2, -1, 0, 1, 2, 3
            */
            if(!action && drawVal < 4) {
                System.out.println("IN CPU SCORE REDUCING");
                int val1;
                int val2;
                i = 0;
                while(i < 4 & !action) {
                    // Check if the first card in a column is revealed, not cleared, and either -2, -1 or 0
                    if((!board.getGrid()[0][i].getIsCleared() && board.getGrid()[0][i].getRevealed()) && (board.getGrid()[0][i].getValue() == -2 || board.getGrid()[0][i].getValue() == -1 ||board.getGrid()[0][i].getValue() == -0)) {
                        
                        // Check the values of the other two cards in the column
                        if(board.getGrid()[1][i].getRevealed()){
                            val1 = board.getGrid()[1][i].getValue();
                        } else {
                            val1 = 13;
                        }
                            
                        if(board.getGrid()[2][i].getRevealed()){
                            val2 = board.getGrid()[2][i].getValue();
                        } else {
                            val2 = 13;
                        }
                        
                        // Swap the score minimizing card with the lowest value
                        //  - If that lowest value is 0 or negative, move to next column
                        //  - Ensure that columns aren't cleared from this action
                        if(val1 > val2 && val1 > 0) {
                            System.out.println("swap row 2");
                            stage.setSwapFlag(true);
                            stage.cardAction(i + 4);  
                            action = true;     
                        } else if(val1 < val2 && val2 > 0){
                            System.out.println("swap row 3");
                            stage.setSwapFlag(true);
                            stage.cardAction(i + 8);
                            action = true;  
                        // Same value for both cards, or drawn card is greater than 0
                        } else {
                            // If both cards are not revealed, swap the with the second card
                            if(val1 == 13 && val2 == 13) {
                                System.out.println("swap row 2");
                                stage.setSwapFlag(true);
                                stage.cardAction(i + 4);  
                                action = true;
                            // Check if the second card is 0, -1 or -2, if so swap the third card with the drawn 1, 2 or 3    
                            } else if((!board.getGrid()[1][i].getIsCleared() && board.getGrid()[1][i].getRevealed()) && (board.getGrid()[1][i].getValue() == -2 || board.getGrid()[1][i].getValue() == -1 ||board.getGrid()[1][i].getValue() == -0)) {
                                // If the remaining row isn't 0, -1 or -2, swap it
                                if((!board.getGrid()[2][i].getIsCleared() && board.getGrid()[2][i].getRevealed()) && !(board.getGrid()[2][i].getValue() == -2 || board.getGrid()[2][i].getValue() == -1 ||board.getGrid()[2][i].getValue() == -0)  && drawVal < board.getGrid()[2][i].getValue()) {
                                    System.out.println("swap row 3");
                                    stage.setSwapFlag(true);
                                    stage.cardAction(i + 8);  
                                    action = true;
                                }
                            // Check if the third card is 0, -1 or -2, if so swap the second card with the drawn 1, 2 or 3 
                            } else if((!board.getGrid()[2][i].getIsCleared() && board.getGrid()[2][i].getRevealed()) && (board.getGrid()[2][i].getValue() == -2 || board.getGrid()[2][i].getValue() == -1 ||board.getGrid()[2][i].getValue() == -0)) {
                                // If the remaining row isn't 0, -1 or -2, swap it
                                if((!board.getGrid()[1][i].getIsCleared() && board.getGrid()[1][i].getRevealed()) && !(board.getGrid()[1][i].getValue() == -2 || board.getGrid()[1][i].getValue() == -1 ||board.getGrid()[1][i].getValue() == -0)  && drawVal < board.getGrid()[1][i].getValue()) {
                                    System.out.println("swap row 2");
                                    stage.setSwapFlag(true);
                                    stage.cardAction(i + 4);  
                                    action = true;
                                }
                            }
                        } 
                        
                    // Check if the second card in a column is revealed, not cleared, and either -2, -1 or 0    
                    } else if((!board.getGrid()[1][i].getIsCleared() && board.getGrid()[1][i].getRevealed()) && (board.getGrid()[1][i].getValue() == -2 || board.getGrid()[1][i].getValue() == -1 ||board.getGrid()[1][i].getValue() == -0)) {
                        
                        // Check the values of the other two cards in the column
                        if(board.getGrid()[0][i].getRevealed()){
                            val1 = board.getGrid()[0][i].getValue();
                        } else {
                            val1 = 13;
                        }
                            
                        if(board.getGrid()[2][i].getRevealed()){
                            val2 = board.getGrid()[2][i].getValue();
                        } else {
                            val2 = 13;
                        }
                        
                        // Swap the score minimizing card with the lowest value
                        //  - If that lowest value is 0 or negative, move to next column
                        //  - Ensure that columns aren't cleared from this action
                        if(val1 > val2 && val1 > 0) {
                            System.out.println("swap row 1");
                            stage.setSwapFlag(true);
                            stage.cardAction(i);  
                            action = true;     
                        } else if(val1 < val2 && val2 > 0){
                            System.out.println("swap row 3");
                            stage.setSwapFlag(true);
                            stage.cardAction(i + 8);
                            action = true;  
                        // Same value for both cards, or drawn card is greater than 0
                        } else {
                            // If both cards are not revealed, swap the with the first card
                            if(val1 == 13 && val2 == 13) {
                                System.out.println("swap row 1");
                                stage.setSwapFlag(true);
                                stage.cardAction(i);  
                                action = true;
                            // Check if the second card is 0, -1 or -2, if so swap the third card with the drawn 1, 2 or 3    
                            } else if((!board.getGrid()[0][i].getIsCleared() && board.getGrid()[0][i].getRevealed()) && (board.getGrid()[0][i].getValue() == -2 || board.getGrid()[0][i].getValue() == -1 ||board.getGrid()[0][i].getValue() == -0)) {
                                // If the remaining row isn't 0, -1 or -2, swap it
                                if((!board.getGrid()[2][i].getIsCleared() && board.getGrid()[2][i].getRevealed()) && !(board.getGrid()[2][i].getValue() == -2 || board.getGrid()[2][i].getValue() == -1 ||board.getGrid()[2][i].getValue() == -0)  && drawVal < board.getGrid()[2][i].getValue()) {
                                    System.out.println("swap row 3");
                                    stage.setSwapFlag(true);
                                    stage.cardAction(i + 8);  
                                    action = true;
                                }
                            // Check if the third card is 0, -1 or -2, if so swap the second card with the drawn 1, 2 or 3 
                            } else if((!board.getGrid()[2][i].getIsCleared() && board.getGrid()[2][i].getRevealed()) && (board.getGrid()[2][i].getValue() == -2 || board.getGrid()[2][i].getValue() == -1 ||board.getGrid()[2][i].getValue() == -0)) {
                                // If the remaining row isn't 0, -1 or -2, swap it
                                if((!board.getGrid()[0][i].getIsCleared() && board.getGrid()[0][i].getRevealed()) && !(board.getGrid()[0][i].getValue() == -2 || board.getGrid()[0][i].getValue() == -1 ||board.getGrid()[0][i].getValue() == -0)  && drawVal < board.getGrid()[0][i].getValue()) {
                                    System.out.println("swap row 1");
                                    stage.setSwapFlag(true);
                                    stage.cardAction(i);  
                                    action = true;
                                }
                            }
                        }    
                    // Check if the third card in a column is revealed, not cleared, and either -2, -1 or 0        
                    } else if((!board.getGrid()[2][i].getIsCleared() && board.getGrid()[2][i].getRevealed()) && (board.getGrid()[2][i].getValue() == -2 || board.getGrid()[2][i].getValue() == -1 ||board.getGrid()[2][i].getValue() == -0)) {
                    
                        // Check the values of the other two cards in the column
                        if(board.getGrid()[0][i].getRevealed()){
                            val1 = board.getGrid()[0][i].getValue();
                        } else {
                            val1 = 13;
                        }
                            
                        if(board.getGrid()[1][i].getRevealed()){
                            val2 = board.getGrid()[1][i].getValue();
                        } else {
                            val2 = 13;
                        }
                        
                        // Swap the score minimizing card with the lowest value
                        //  - If that lowest value is 0 or negative, move to next column
                        //  - Ensure that columns aren't cleared from this action
                        if(val1 > val2 && val1 > 0) {
                            System.out.println("swap row 1");
                            stage.setSwapFlag(true);
                            stage.cardAction(i);  
                            action = true;     
                        } else if(val1 < val2 && val2 > 0){
                            System.out.println("swap row 2");
                            stage.setSwapFlag(true);
                            stage.cardAction(i + 4);
                            action = true;  
                        // Same value for both cards, or drawn card is greater than 0
                        } else {
                            // If both cards are not revealed, swap the with the first card
                            if(val1 == 13 && val2 == 13) {
                                System.out.println("swap row 1");
                                stage.setSwapFlag(true);
                                stage.cardAction(i);  
                                action = true;
                            // Check if the second card is 0, -1 or -2, if so swap the third card with the drawn 1, 2 or 3    
                            } else if((!board.getGrid()[0][i].getIsCleared() && board.getGrid()[0][i].getRevealed()) && (board.getGrid()[0][i].getValue() == -2 || board.getGrid()[0][i].getValue() == -1 ||board.getGrid()[0][i].getValue() == -0)) {
                                // If the remaining row isn't 0, -1 or -2, swap it
                                if((!board.getGrid()[1][i].getIsCleared() && board.getGrid()[1][i].getRevealed()) && !(board.getGrid()[1][i].getValue() == -2 || board.getGrid()[1][i].getValue() == -1 ||board.getGrid()[1][i].getValue() == -0)  && drawVal < board.getGrid()[1][i].getValue()) {
                                    System.out.println("swap row 2");
                                    stage.setSwapFlag(true);
                                    stage.cardAction(i + 8);  
                                    action = true;
                                }
                            // Check if the third card is 0, -1 or -2, if so swap the second card with the drawn 1, 2 or 3 
                            } else if((!board.getGrid()[1][i].getIsCleared() && board.getGrid()[1][i].getRevealed()) && (board.getGrid()[1][i].getValue() == -2 || board.getGrid()[1][i].getValue() == -1 ||board.getGrid()[1][i].getValue() == -0)) {
                                // If the remaining row isn't 0, -1 or -2, swap it
                                if((!board.getGrid()[0][i].getIsCleared() && board.getGrid()[0][i].getRevealed()) && !(board.getGrid()[0][i].getValue() == -2 || board.getGrid()[0][i].getValue() == -1 ||board.getGrid()[0][i].getValue() == -0) && drawVal < board.getGrid()[0][i].getValue()) {
                                        System.out.println("swap row 1");
                                        stage.setSwapFlag(true);
                                        stage.cardAction(i);  
                                        action = true;
                                }
                            }
                        }    
                    }
                i++;
                }
            }
        }
        });
            timer.setRepeats(false);
            timer.start(); 
    }
    
    public void beginTurn() {
        // Check if it's a CPU turn and play their action
        if(!stage.bots.get(getCurrentPlayerVal())){
            cpuTurn();
        }
    }     
}



