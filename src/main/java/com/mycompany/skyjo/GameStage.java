/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.skyjo;

import com.mycompany.skyjo.Game.InvalidPlayerTurnException;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Admin
 */
public class GameStage extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GameStage.class.getName());
        private AddPlayerNames addPlayers = new AddPlayerNames();
        ArrayList<String> temp = new ArrayList<>();
        String[] pids;
        Game game;
        ArrayList<JButton> cardButtons = new ArrayList<JButton>();   
        ArrayList<String> cardIds;
        PopUp window;
        boolean swapFlag;
        boolean hasDrawn;
    
    public GameStage() {
    }
        
    public GameStage(ArrayList<String> playerIds) {
        initComponents();
        temp = playerIds;
        pids = temp.toArray(new String[temp.size()]);
        game = new Game(pids);
        populateArrayList();
        game.start(game);
        setPidName();
        drawCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/PNGs/Card_Back4.png"))); // may have to remove getclass().getResource(
        ImageIcon icon = (ImageIcon) drawCard.getIcon();
        //currCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/PNGs/Green_3.png"))); // may have to remove getclass().getResource(
        
        String file = game.getTopCardImage(game.getDiscardTop()) + ".png";

        currCard.setIcon(new ImageIcon(
        getClass().getResource("/images/PNGs/" + file)
        ));
        
        
        setButtonIcons();
        swapFlag = false;
        hasDrawn = false;    // make sure player draws a card before flipping
    }
    
    public void setButtonIcons(){
        //String listString = ""; //is this ever used?
        SkyjoCard[][] currGrid = game.getPlayerBoard(game.getCurrentPlayer()).getGrid();
        System.out.println(game.getCurrentPlayer());
        //String[] cardNames = new String[12];
        int counter = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 4; j++){
                System.out.printf(
                "%s [%d,%d] value=%d revealed=%b cleared=%b id=%d%n",
                game.getCurrentPlayer(),
                i,
                j,
                currGrid[i][j].getValue(),
                currGrid[i][j].getRevealed(),
                currGrid[i][j].getIsCleared(),
                System.identityHashCode(currGrid[i][j])
); 
               if(currGrid[i][j].getIsCleared()){
                   System.out.println("button #" + counter + " set invisible");
                   cardButtons.get(counter).setVisible(false);
               } else {
                cardButtons.get(counter).setVisible(true);
                if(currGrid[i][j].getRevealed()){
                    String file = currGrid[i][j].toString() + ".png";
                     cardButtons.get(counter).setIcon(new ImageIcon(
                     getClass().getResource("/images/PNGs/" + file)
                     ));
                } else {
                    cardButtons.get(counter).setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/PNGs/Card_Back4.png"))); 
                }
               }
               //cardNames[i + j] = currGrid[i][j].toString();
               //listString += currGrid[i][j].toString();
               //listString += ",";
               counter = counter + 1;
            }
        }
        
        //cardIds = new ArrayList<>(Arrays.asList(cardNames));
        
    }
    
    public void populateArrayList(){
        cardButtons.add(card1);
        cardButtons.add(card2);
        cardButtons.add(card3);
        cardButtons.add(card4);
        cardButtons.add(card5);
        cardButtons.add(card6);
        cardButtons.add(card7);
        cardButtons.add(card8);
        cardButtons.add(card9);
        cardButtons.add(card10);
        cardButtons.add(card11);
        cardButtons.add(card12);    
    }
    
    public void setPidName(){
        String currentPlayer = game.getCurrentPlayer();
        playerLabel.setText(currentPlayer + "'s Cards");
    }
    
    public void setPidName(String currentPlayer){
        playerLabel.setText(currentPlayer + "'s Cards");
    }
    
    private void cardAction(int index){
        SkyjoCard[][] currGrid = game.getPlayerBoard(game.getCurrentPlayer()).getGrid();
        SkyjoBoard currBoard = game.getPlayerBoard(game.getCurrentPlayer());
        int rowIndex = (index / 4);
        int colIndex = (index % 4);
        System.out.println("Clicked row=" + rowIndex + " col=" + colIndex);
        int playerIndex = game.getCurrentPlayerVal();
        if(currGrid[rowIndex][colIndex].getRevealed() && swapFlag == false){
            JLabel message = new JLabel("You cannot flip a revealed card!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null, message);
        } else if(swapFlag == false && hasDrawn == false && (currBoard.revealedCount() >= 2)) {
            JLabel message = new JLabel("You must draw before flipping!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null, message);
        } else if(currBoard.revealedCount() < 2 && swapFlag) {
            JLabel message = new JLabel("You must flip until you have 2 cards revealed!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null, message);
        } else {
            try {
                if((game.getTurnCount() / (game.getCurrentPlayerVal() + 1)) == 1) {
                    game.submitAction(game.getCurrentPlayer(), colIndex, rowIndex, false);  
                } else {
                    game.submitAction(game.getCurrentPlayer(), colIndex, rowIndex, swapFlag);            
                }
                String file = currGrid[rowIndex][colIndex] + ".png";
                cardButtons.get(index).setIcon(new ImageIcon(
                getClass().getResource("/images/PNGs/" + file)
                ));
            } catch(InvalidPlayerTurnException e) {
                Logger.getLogger(GameStage.class.getName()).log(Level.SEVERE,null,e);
            }
            System.out.println("current player index = " + playerIndex);
            System.out.println("turn count = " + game.getTurnCount());
            System.out.println((playerIndex == game.getPlayers().length - 1) + "," + (currBoard.revealedCount() == 2));
            // If the last player has flipped their two cards, start the game according to determined turn order.
            if((playerIndex == game.getPlayers().length - 1) && (currBoard.revealedCount() == 2)) {
                game.startNewRound();
            }
            swapFlag = false;
            hasDrawn = false;
            drawButton.setVisible(true);
            this.setPidName(game.getCurrentPlayer());
            setButtonIcons();
            String file = game.getTopCardImage(game.getDiscardTop()) + ".png";

            currCard.setIcon(new ImageIcon(
            getClass().getResource("/images/PNGs/" + file)
            ));
        } 
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        card1 = new javax.swing.JButton();
        card2 = new javax.swing.JButton();
        card3 = new javax.swing.JButton();
        card4 = new javax.swing.JButton();
        card5 = new javax.swing.JButton();
        card6 = new javax.swing.JButton();
        card7 = new javax.swing.JButton();
        card8 = new javax.swing.JButton();
        card9 = new javax.swing.JButton();
        card10 = new javax.swing.JButton();
        card11 = new javax.swing.JButton();
        card12 = new javax.swing.JButton();
        playerLabel = new javax.swing.JLabel();
        currCard = new javax.swing.JButton();
        swapButton = new javax.swing.JButton();
        flipButton = new javax.swing.JButton();
        drawCard = new javax.swing.JButton();
        drawButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 500));

        card1.setMaximumSize(new java.awt.Dimension(75, 100));
        card1.setMinimumSize(new java.awt.Dimension(75, 100));
        card1.setName(""); // NOI18N
        card1.setPreferredSize(new java.awt.Dimension(75, 100));
        card1.addActionListener(this::card1ActionPerformed);

        card2.setMaximumSize(new java.awt.Dimension(75, 100));
        card2.setMinimumSize(new java.awt.Dimension(75, 100));
        card2.setName(""); // NOI18N
        card2.setPreferredSize(new java.awt.Dimension(75, 100));
        card2.addActionListener(this::card2ActionPerformed);

        card3.setMaximumSize(new java.awt.Dimension(75, 100));
        card3.setMinimumSize(new java.awt.Dimension(75, 100));
        card3.setName(""); // NOI18N
        card3.setPreferredSize(new java.awt.Dimension(75, 100));
        card3.addActionListener(this::card3ActionPerformed);

        card4.setMaximumSize(new java.awt.Dimension(75, 100));
        card4.setMinimumSize(new java.awt.Dimension(75, 100));
        card4.setName(""); // NOI18N
        card4.setPreferredSize(new java.awt.Dimension(75, 100));
        card4.addActionListener(this::card4ActionPerformed);

        card5.setMaximumSize(new java.awt.Dimension(75, 100));
        card5.setMinimumSize(new java.awt.Dimension(75, 100));
        card5.setName(""); // NOI18N
        card5.setPreferredSize(new java.awt.Dimension(75, 100));
        card5.addActionListener(this::card5ActionPerformed);

        card6.setMaximumSize(new java.awt.Dimension(75, 100));
        card6.setMinimumSize(new java.awt.Dimension(75, 100));
        card6.setName(""); // NOI18N
        card6.setPreferredSize(new java.awt.Dimension(75, 100));
        card6.addActionListener(this::card6ActionPerformed);

        card7.setMaximumSize(new java.awt.Dimension(75, 100));
        card7.setMinimumSize(new java.awt.Dimension(75, 100));
        card7.setName(""); // NOI18N
        card7.setPreferredSize(new java.awt.Dimension(75, 100));
        card7.addActionListener(this::card7ActionPerformed);

        card8.setMaximumSize(new java.awt.Dimension(75, 100));
        card8.setMinimumSize(new java.awt.Dimension(75, 100));
        card8.setName(""); // NOI18N
        card8.setPreferredSize(new java.awt.Dimension(75, 100));
        card8.addActionListener(this::card8ActionPerformed);

        card9.setMaximumSize(new java.awt.Dimension(75, 100));
        card9.setMinimumSize(new java.awt.Dimension(75, 100));
        card9.setName(""); // NOI18N
        card9.setPreferredSize(new java.awt.Dimension(75, 100));
        card9.addActionListener(this::card9ActionPerformed);

        card10.setMaximumSize(new java.awt.Dimension(75, 100));
        card10.setMinimumSize(new java.awt.Dimension(75, 100));
        card10.setName(""); // NOI18N
        card10.setPreferredSize(new java.awt.Dimension(75, 100));
        card10.addActionListener(this::card10ActionPerformed);

        card11.setMaximumSize(new java.awt.Dimension(75, 100));
        card11.setMinimumSize(new java.awt.Dimension(75, 100));
        card11.setName(""); // NOI18N
        card11.setPreferredSize(new java.awt.Dimension(75, 100));
        card11.addActionListener(this::card11ActionPerformed);

        card12.setMaximumSize(new java.awt.Dimension(75, 100));
        card12.setMinimumSize(new java.awt.Dimension(75, 100));
        card12.setName(""); // NOI18N
        card12.setPreferredSize(new java.awt.Dimension(75, 100));
        card12.addActionListener(this::card12ActionPerformed);

        playerLabel.setFont(new java.awt.Font("Rockwell", 0, 48)); // NOI18N
        playerLabel.setText("gdfsgfdgdfgss's Cards");

        currCard.setMaximumSize(new java.awt.Dimension(75, 100));
        currCard.setMinimumSize(new java.awt.Dimension(75, 100));
        currCard.setPreferredSize(new java.awt.Dimension(75, 100));

        swapButton.setText("SWAP");
        swapButton.addActionListener(this::swapButtonActionPerformed);

        flipButton.setText("FLIP");
        flipButton.addActionListener(this::flipButtonActionPerformed);

        drawCard.setAlignmentY(0.0F);
        drawCard.setIconTextGap(0);
        drawCard.setMargin(new java.awt.Insets(0, 0, 0, 0));
        drawCard.setMaximumSize(new java.awt.Dimension(75, 100));
        drawCard.setMinimumSize(new java.awt.Dimension(75, 100));
        drawCard.setPreferredSize(new java.awt.Dimension(75, 100));
        drawCard.addActionListener(this::drawCardActionPerformed);

        drawButton.setText("DRAW");
        drawButton.addActionListener(this::drawButtonActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(drawButton)
                            .addComponent(drawCard, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(swapButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(flipButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(currCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(79, 79, 79)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(card5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(card9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(card12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(239, 239, 239))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(playerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(123, 123, 123))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(card5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(currCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(drawCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(swapButton)
                            .addComponent(drawButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flipButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(card9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(playerLabel)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 901, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void drawButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawButtonActionPerformed
        SkyjoBoard currBoard = game.getPlayerBoard(game.getCurrentPlayer());
        if(currBoard.revealedCount() < 2){
            JLabel message = new JLabel("You must flip until you have 2 cards revealed!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null, message);
        } else {
            try {
                game.submitDraw(game.getCurrentPlayer());
            } catch(InvalidPlayerTurnException e) {
                Logger.getLogger(GameStage.class.getName()).log(Level.SEVERE,null,e);
            }
            String file = game.getTopCardImage(game.getDiscardTop()) + ".png";

            currCard.setIcon(new ImageIcon(
            getClass().getResource("/images/PNGs/" + file)
            ));
            drawButton.setVisible(false);
            hasDrawn = true;
        }
    }//GEN-LAST:event_drawButtonActionPerformed

    private void card1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card1ActionPerformed
        cardAction(0);
    }//GEN-LAST:event_card1ActionPerformed

    private void card2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card2ActionPerformed
        cardAction(1);
    }//GEN-LAST:event_card2ActionPerformed

    private void card3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card3ActionPerformed
        cardAction(2);
    }//GEN-LAST:event_card3ActionPerformed

    private void card4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card4ActionPerformed
        cardAction(3);
    }//GEN-LAST:event_card4ActionPerformed

    private void card5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card5ActionPerformed
        cardAction(4);
    }//GEN-LAST:event_card5ActionPerformed

    private void card6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card6ActionPerformed
        cardAction(5);
    }//GEN-LAST:event_card6ActionPerformed

    private void card7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card7ActionPerformed
        cardAction(6);
    }//GEN-LAST:event_card7ActionPerformed

    private void card8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card8ActionPerformed
        cardAction(7);
    }//GEN-LAST:event_card8ActionPerformed

    private void card9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card9ActionPerformed
        cardAction(8);
    }//GEN-LAST:event_card9ActionPerformed

    private void card10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card10ActionPerformed
        cardAction(9);
    }//GEN-LAST:event_card10ActionPerformed

    private void card11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card11ActionPerformed
        cardAction(10);
    }//GEN-LAST:event_card11ActionPerformed

    private void card12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_card12ActionPerformed
        cardAction(11);
    }//GEN-LAST:event_card12ActionPerformed

    private void drawCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawCardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drawCardActionPerformed

    private void swapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swapButtonActionPerformed
        swapFlag = true;
    }//GEN-LAST:event_swapButtonActionPerformed

    private void flipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flipButtonActionPerformed
        swapFlag = false;
    }//GEN-LAST:event_flipButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GameStage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton card1;
    private javax.swing.JButton card10;
    private javax.swing.JButton card11;
    private javax.swing.JButton card12;
    private javax.swing.JButton card2;
    private javax.swing.JButton card3;
    private javax.swing.JButton card4;
    private javax.swing.JButton card5;
    private javax.swing.JButton card6;
    private javax.swing.JButton card7;
    private javax.swing.JButton card8;
    private javax.swing.JButton card9;
    private javax.swing.JButton currCard;
    private javax.swing.JButton drawButton;
    private javax.swing.JButton drawCard;
    private javax.swing.JButton flipButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel playerLabel;
    private javax.swing.JButton swapButton;
    // End of variables declaration//GEN-END:variables
}
