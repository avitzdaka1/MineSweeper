package com.omeryaari.minesweeper.logic;

public class Tile {
    private int value;
    private boolean flagged;
    private boolean visible;

    public Tile(int value) {
        this.value = value;
        flagged = false;
        visible = false;
    }
    public boolean isVisible() {
        return visible;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void unveil() {
        visible = true;
    }

    public void flag() {
        flagged = true;
    }

    public void unFlag() {
        flagged = false;
    }
}
