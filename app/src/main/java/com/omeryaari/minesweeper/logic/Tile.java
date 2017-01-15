package com.omeryaari.minesweeper.logic;

public class Tile {

    //  Values for tiles, numbers 1-8 (including) represent number tiles.
    public enum TileProperty {
        Empty, One, Two, Three, Four, Five, Six, Seven, Eight, Flag, Mine, Invisible;
    }

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
        if (this.value != TileProperty.Mine.ordinal())
            this.value = value;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void hide() {
        visible = false;
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
