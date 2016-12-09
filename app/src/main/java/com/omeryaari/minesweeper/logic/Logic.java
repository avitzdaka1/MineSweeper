package com.omeryaari.minesweeper.logic;

import android.util.Log;

import java.util.Random;

public class Logic {
    //  Values for tiles, numbers 1-8 (including) represent number tiles.
    public static final int TILE_EMPTY = 0;
    public static final int TILE_FLAG = 9;
    public static final int TILE_MINE = 10;
    // Easy level configurations.
    public static final int LEVEL_EASY = 0;
    public static final int LEVEL_EASY_MINES = 5;
    public static final int LEVEL_EASY_SIZE = 10;
    // Normal level configurations.
    public static final int LEVEL_NORMAL = 1;
    public static final int LEVEL_NORMAL_MINES = 10;
    public static final int LEVEL_NORMAL_SIZE = 10;
    //  Hard level configurations.
    public static final int LEVEL_HARD = 2;
    public static final int LEVEL_HARD_MINES = 10;
    public static final int LEVEL_HARD_SIZE = 5;
    public static final int CLICK_TYPE_MINE = 0;
    public static final int CLICK_TYPE_FLAG = 1;
    //  Finals used for in the TimerTask.
    public static final int MILLISECONDS_IN_SECOND = 1000;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int UI_DO_NOTHING = -1;
    //  Game outcomes (win or loss).
    public static final int OUTCOME_LOSS = 0;
    public static final int OUTCOME_WIN = 1;
    private final String TAG = Logic.class.getSimpleName();

    private Tile[][] logicBoard;
    private int numOfMines;
    private int flagsCount;
    private int difficulty;
    private int clickType;
    private int timerSeconds;
    private int timerMinutes;
    private TimerChangedListener timerListener;
    private RefreshBoardListener refreshListener;

    public Logic(int difficulty) {
        this.difficulty = difficulty;
        initGame();
    }

    //  Returns the size of the game board.
    public int getSize() {
        return logicBoard.length;
    }

    //  Unveils blank and number tiles after a blank tile has been clicked, recursively.
    public void unveilBlanksAndNumbers(int row, int col) {
        Tile tempTile = logicBoard[row][col];
        //  If tile is already visible, do nothing.
        if (tempTile.getValue() != TILE_MINE)
            tempTile.unveil();

        if (tempTile.getValue() == TILE_EMPTY){
            if (row != logicBoard.length - 1 && !logicBoard[row + 1][col].isVisible())
                unveilBlanksAndNumbers(row + 1, col);
            if (row != 0  && !logicBoard[row - 1][col].isVisible())
                unveilBlanksAndNumbers(row - 1, col);
            if (col != logicBoard.length - 1  && !logicBoard[row][col + 1].isVisible())
                unveilBlanksAndNumbers(row, col + 1);
            if (col != 0 && !logicBoard[row][col - 1].isVisible())
                unveilBlanksAndNumbers(row, col - 1);
        }
    }

    //  Called upon by the GameActivity when a tile is clicked.
    public int checkTile(int row, int col) {
        Tile tempTile = logicBoard[row][col];
        int value = tempTile.getValue();
        ////  If tile is already visible, return tell the UI to do nothing.
       // if (tempTile.isVisible())
       //     return TILE_NOT_VISIBLE;
        //  If the click type is mine.
        if (clickType == CLICK_TYPE_MINE) {
                switch (value) {
                    case TILE_EMPTY: {
                        if (!tempTile.isVisible()) {
                            unveilBlanksAndNumbers(row, col);
                            refreshListener.refreshBoard();
                        }
                        break;
                    }
                    case TILE_MINE: {
                        endGame(OUTCOME_LOSS);
                        break;
                    }
                    default:
                        break;
                }
            tempTile.unveil();
        }
        //  If the click type is flag.
        else if (clickType == CLICK_TYPE_FLAG) {
            if (!tempTile.isVisible()) {
                if (tempTile.isFlagged()) {
                    tempTile.unFlag();
                    flagsCount--;
                    value = UI_DO_NOTHING;
                }
                else {
                    tempTile.flag();
                    flagsCount++;
                    if (flagsCount == numOfMines) {
                        //  TODO:   Check if all mines are flagged for victory.
                    }
                    value = TILE_FLAG;
                }
            }
        }
        return value;
    }

    public int[][] getIntBoard() {
        int[][] intBoard = new int[logicBoard.length][logicBoard[0].length];
        for(int row = 0; row < logicBoard.length; row ++) {
            for (int col = 0; col < logicBoard[0].length; col++) {
                if (logicBoard[row][col].isVisible())
                    intBoard[row][col] = logicBoard[row][col].getValue();
                else
                    intBoard[row][col] = UI_DO_NOTHING;
            }
        }
        return intBoard;
    }

    private void endGame(int outcome) {

    }

    //  Initializes the game, logic wise.
    private void initGame() {
        switch (difficulty) {
            case LEVEL_EASY: {
                numOfMines = LEVEL_EASY_MINES;
                logicBoard = new Tile[LEVEL_EASY_SIZE][LEVEL_EASY_SIZE];
                createTiles();
                break;
            }
            case LEVEL_NORMAL: {
                numOfMines = LEVEL_NORMAL_MINES;
                logicBoard = new Tile[LEVEL_NORMAL_SIZE][LEVEL_NORMAL_SIZE];
                createTiles();
                break;
            }
            case LEVEL_HARD: {
                numOfMines = LEVEL_HARD_MINES;
                logicBoard = new Tile[LEVEL_HARD_SIZE][LEVEL_HARD_SIZE];
                createTiles();
                break;
            }
            default:
                break;
        }
        flagsCount = 0;
        TimerTask timerTask = new TimerTask();
        Thread timerThread = new Thread(timerTask);
        timerThread.start();
    }

    //  Creates the Tile array.
    private void createTiles() {
        for(int row = 0; row < logicBoard.length; row++) {
            for(int col = 0; col < logicBoard[0].length; col++) {
                logicBoard[row][col] = new Tile(TILE_EMPTY);
            }
        }
        randomizeMines();
    }

    //  Creates number tiles around a given mine tile.
    private void createNumberTiles(int row, int col) {
            if (row != 0) {
                logicBoard[row - 1][col].setValue(logicBoard[row - 1][col].getValue() + 1);
                if (col != 0)
                    logicBoard[row - 1][col - 1].setValue(logicBoard[row - 1][col - 1].getValue() + 1);
                if (col != logicBoard.length - 1)
                    logicBoard[row - 1][col + 1].setValue(logicBoard[row - 1][col + 1].getValue() + 1);
            }
            if (row != logicBoard.length - 1) {
                logicBoard[row + 1][col].setValue(logicBoard[row + 1][col].getValue() + 1);
                if (col != 0)
                    logicBoard[row + 1][col - 1].setValue(logicBoard[row + 1][col - 1].getValue() + 1);
                if (col != logicBoard.length - 1)
                    logicBoard[row + 1][col + 1].setValue(logicBoard[row + 1][col + 1].getValue() + 1);
            }
            if (col != 0)
                logicBoard[row][col - 1].setValue(logicBoard[row][col - 1].getValue() + 1);
            if (col != logicBoard.length - 1)
                logicBoard[row][col + 1].setValue(logicBoard[row][col + 1].getValue() + 1);
    }

    //  Generates mines and places them in the board.
    private void randomizeMines() {
        int minesLeftToPlace = numOfMines;
        int row, col;
        Random rand = new Random();
        while (minesLeftToPlace > 0) {
            row = rand.nextInt(logicBoard.length);
            col = rand.nextInt(logicBoard.length);
            Tile tempTile = logicBoard[row][col];
            if (tempTile.getValue() != TILE_MINE) {
                tempTile.setValue(TILE_MINE);
                createNumberTiles(row, col);
                minesLeftToPlace--;
            }
        }
    }

    private class TimerTask implements Runnable {
        @Override
        public void run() {
            timerSeconds = 0;
            timerMinutes = 0;
            try {
                while (true) {
                    Thread.sleep(MILLISECONDS_IN_SECOND);
                    if (timerSeconds < SECONDS_IN_MINUTE-1)
                        timerSeconds++;
                    //  If a minute has passed.
                    else {
                        timerSeconds = 0;
                        timerMinutes++;
                    }
                    timerListener.timeChanged();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getMinutes() {
        return timerMinutes;
    }

    public int getSeconds() {
        return timerSeconds;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }

    public void setTimerListener(TimerChangedListener timerListener) {
        this.timerListener = timerListener;
    }

    public void setRefreshBoardListener(RefreshBoardListener refreshListener) {
        this.refreshListener = refreshListener;
    }
}