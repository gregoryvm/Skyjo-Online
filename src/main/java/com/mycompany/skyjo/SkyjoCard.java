/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.skyjo;

public class SkyjoCard {
    enum Color
    {
        Red, Yellow, Green, Teal, Blue;
      
        private static final Color[] colors = Color.values();
    }
    private boolean revealed;
    private boolean isCleared;
    private final int value;

    public SkyjoCard(final int value) {
        this.revealed = false;
        this.value = value;
    }
    
    public SkyjoCard(SkyjoCard other) {
        this.revealed = other.revealed;
        this.value = other.value;
        this.isCleared = false;
    }
    
    public int getValue() {
        return this.value;
    }

    public Color getColor() {
        int colorValue = getValue();
        int returnColor = 0;
        switch (colorValue) {
            case 12: returnColor = 0;
            break;
            case 11: returnColor = 0;
            break;
            case 10: returnColor = 0;
            break;
            case 9: returnColor = 0;
            break;
            case 8: returnColor = 1;
            break;
            case 7: returnColor = 1;
            break;
            case 6: returnColor = 1;
            break;
            case 5: returnColor = 1;
            break;
            case 4: returnColor = 2;
            break;
            case 3: returnColor = 2;
            break;
            case 2: returnColor = 2;
            break;
            case 1: returnColor = 2;
            break;
            case 0: returnColor = 3;
            break;
            case -1: returnColor = 4;
            break;
            case -2: returnColor = 4;
            break;
        }
        return Color.colors[returnColor];
    }

    public boolean getIsCleared() {
        return this.isCleared;
    }
    public void setIsCleared(boolean newCleared) {
        this.isCleared = newCleared;
    }

    public boolean getRevealed() {
        return this.revealed;
    }
    public void revealCard() {
        this.revealed = true;
    }

    public String toString() {
        return getColor() + "_" + this.value;
    }

}
