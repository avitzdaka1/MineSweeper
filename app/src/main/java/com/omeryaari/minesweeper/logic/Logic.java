package com.omeryaari.minesweeper.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Logic {

    //  Level configurations.
    public enum LevelProperty {
        Easy(0), Easy_Mines(5), Easy_Size(10), Normal(1), Normal_Mines(10), Normal_Size(10), Hard(2), Hard_Mines(10), Hard_Size(5);
        private int value;
        LevelProperty(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public static final int CLICK_TYPE_MINE = 0;
    public static final int CLICK_TYPE_FLAG = 1;
    public static final int MINES_TO_ADD_WHEN_TILTED = 3;
    //  Finals used for in the TimerTask.
    public static final int TIMER_SLEEP_TIME = 1000;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int TILE_INVISIBLE = -1;
    //  Game outcomes (win or loss).
    public static final int OUTCOME_LOSS = 0;
    public static final int OUTCOME_WIN = 1;
    private Tile[][] logicBoard;
    private int numOfMines;
    private int flagsCount;
    private int difficulty;
    private int clickType;
    private int timerSeconds;
    private int timerMinutes;
    private List<TimerChangedListener> timerListeners = new ArrayList<>();
    private RefreshBoardListener refreshListener;
    private EndGameListener endGameListener;
    private MinesUpdateListener minesUpdateListener;
    private Thread timerThread;

    public Logic(int difficulty) {
        this.difficulty = difficulty;
        initGame();
    }

    //  Returns the size of the game board.
    public int getSize() {
        return logicBoard.length;
    }

    //  Unveils blank and number tiles after a blank tile has been clicked, recursively.
    private void unveilBlanksAndNumbers(int row, int col) {
        Tile tempTile = logicBoard[row][col];
        //  If tile is already visible, do nothing.
        if (tempTile.getValue() != Tile.TileProperty.Mine.ordinal())
            tempTile.unveil();

        if (tempTile.getValue() == Tile.TileProperty.Empty.ordinal()){
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
        Tile.TileProperty tempTileProperty = Tile.TileProperty.Invisible;
        for(Tile.TileProperty tile : Tile.TileProperty.values())
                if (tile.ordinal() == value)
                    tempTileProperty = tile;
        //  If the click type is mine.
            if (clickType == CLICK_TYPE_MINE) {
                if (tempTile.isFlagged())
                    value = Tile.TileProperty.Flag.ordinal();
                else {
                    switch (tempTileProperty) {
                        case Empty: {
                            if (!tempTile.isVisible()) {
                                unveilBlanksAndNumbers(row, col);
                                refreshListener.refreshBoard();
                            }
                            break;
                        }
                        case Mine: {
                            endGameListener.onEndGame(OUTCOME_LOSS);
                            break;
                        }
                        default:
                            break;
                    }
                    tempTile.unveil();
                }
            }
        //  If the click type is flag.
        if (clickType == CLICK_TYPE_FLAG) {
            if (!tempTile.isVisible()) {
                if (tempTile.isFlagged()) {
                    tempTile.unFlag();
                    flagsCount--;
                    value = Tile.TileProperty.Invisible.ordinal();
                    minesUpdateListener.minesUpdated();
                }
                else {
                    if (flagsCount < numOfMines) {
                        tempTile.flag();
                        flagsCount++;
                        value = Tile.TileProperty.Flag.ordinal();
                        minesUpdateListener.minesUpdated();
                    }
                    else
                        value = Tile.TileProperty.Invisible.ordinal();
                }
            }
        }
        checkVictory();
        return value;
    }

    private void checkVictory() {
        int minesFlagged = 0;
        for(int row = 0; row < logicBoard.length; row++) {
            for(int col = 0; col < logicBoard[0].length; col++) {
                if (logicBoard[row][col].getValue() == Tile.TileProperty.Mine.ordinal() && logicBoard[row][col].isFlagged())
                    minesFlagged++;
            }
        }
        if (minesFlagged == numOfMines)
            endGameListener.onEndGame(OUTCOME_WIN);
    }

    public int[][] getIntBoard() {
        int[][] intBoard = new int[logicBoard.length][logicBoard[0].length];
        for(int row = 0; row < logicBoard.length; row ++) {
            for (int col = 0; col < logicBoard[0].length; col++) {
                if (logicBoard[row][col].isVisible())
                    intBoard[row][col] = logicBoard[row][col].getValue();
                else
                    intBoard[row][col] = TILE_INVISIBLE;
            }
        }
        return intBoard;
    }

    //  Initializes the game, logic wise.
    private void initGame() {
        LevelProperty tempLevelProperty = LevelProperty.Easy;
        for(LevelProperty level : LevelProperty.values())
            if (difficulty == level.getValue())
                tempLevelProperty = level;
        switch (tempLevelProperty) {
            case Easy: {
                numOfMines = LevelProperty.Easy_Mines.getValue();
                logicBoard = new Tile[LevelProperty.Easy_Size.getValue()][LevelProperty.Easy_Size.getValue()];
                createTiles();
                break;
            }
            case Normal: {
                numOfMines = LevelProperty.Normal_Mines.getValue();
                logicBoard = new Tile[LevelProperty.Normal_Size.getValue()][LevelProperty.Normal_Size.getValue()];
                createTiles();
                break;
            }
            case Hard: {
                numOfMines = LevelProperty.Hard_Mines.getValue();
                logicBoard = new Tile[LevelProperty.Hard_Size.getValue()][LevelProperty.Hard_Size.getValue()];
                createTiles();
                break;
            }
            default:
                break;
        }
        flagsCount = 0;
        TimerTask timerTask = new TimerTask();
        timerThread = new Thread(timerTask);
        timerThread.start();
    }

    //  Creates the Tile array.
    private void createTiles() {
        for(int row = 0; row < logicBoard.length; row++) {
            for(int col = 0; col < logicBoard[0].length; col++) {
                logicBoard[row][col] = new Tile(Tile.TileProperty.Empty.ordinal());
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
            if (tempTile.getValue() != Tile.TileProperty.Mine.ordinal()) {
                tempTile.setValue(Tile.TileProperty.Mine.ordinal());
                createNumberTiles(row, col);
                minesLeftToPlace--;
            }
        }
    }

    //  Adds a single mine every second if the device was tilted.
    public void onTiltDevice(ScreenSide side) {
            boolean mineAdded = false;
            switch (side) {
                case Left:
                    for (int col = 0; col < logicBoard[0].length; col++) {
                        mineAdded = addMineInCol(col);
                        if (mineAdded)
                            break;
                    }
                    break;
                case Right:
                    for (int col = logicBoard[0].length - 1; col > 0; col--) {
                        mineAdded = addMineInCol(col);
                        if (mineAdded)
                            break;
                    }
                    break;
                case Top:
                    for (int row = 0; row < logicBoard.length; row++) {
                        mineAdded = addMineInRow(row);
                        if (mineAdded)
                            break;
                    }
                    break;
                case Bottom:
                    for (int row = logicBoard.length - 1; row > 0; row--) {
                        mineAdded = addMineInRow(row);
                        if (mineAdded)
                            break;
                    }
                    break;
            }
            if (mineAdded) {
                numOfMines++;
                if (numOfMines == logicBoard.length * logicBoard[0].length) {
                    endGameListener.onEndGame(OUTCOME_LOSS);
                }
                minesUpdateListener.minesUpdated();
                refreshListener.refreshBoard();
            }
    }

    //  Attemps to add a mine in the given column.
    private boolean addMineInCol(int col) {
        for(int row = 0; row < logicBoard.length; row++) {
            if (logicBoard[row][col].getValue() != Tile.TileProperty.Mine.ordinal()) {
                logicBoard[row][col].setValue(Tile.TileProperty.Mine.ordinal());
                createNumberTiles(row, col);
                if (logicBoard[row][col].isVisible())
                    logicBoard[row][col].hide();
                return true;
            }
        }
        return false;
    }

    //  Attempts to add a mine in the given row.
    private boolean addMineInRow(int row) {
        for(int col = 0; col < logicBoard[0].length; col++) {
            if (logicBoard[row][col].getValue() != Tile.TileProperty.Mine.ordinal()) {
                logicBoard[row][col].setValue(Tile.TileProperty.Mine.ordinal());
                createNumberTiles(row, col);
                if (logicBoard[row][col].isVisible())
                    logicBoard[row][col].hide();
                return true;
            }
        }
        return false;
    }

    //  Timer task.
    private class TimerTask implements Runnable {
        @Override
        public void run() {
            timerSeconds = 0;
            timerMinutes = 0;
            try {
                while (true) {
                    Thread.sleep(TIMER_SLEEP_TIME);
                    if (timerSeconds < SECONDS_IN_MINUTE-1)
                        timerSeconds++;
                    //  If a minute has passed.
                    else {
                        timerSeconds = 0;
                        timerMinutes++;
                    }
                    for(TimerChangedListener listener : timerListeners)
                        listener.timeChanged();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    //  Returns number of minutes from the timer.
    public int getMinutes() {
        return timerMinutes;
    }

    //  Returns number of seconds from the timer.
    public int getSeconds() {
        return timerSeconds;
    }

    //  Returns number of mines minus the flags count.
    public int getMinesLeft() {
        return numOfMines - flagsCount;
    }

    //  Sets the click type to either Mine or Flag.
    public void setClickType(int clickType) {
        this.clickType = clickType;
    }

    //  Sets the timer listener (which is actually the GameActivity).
    public void setTimerListener(TimerChangedListener timerListener) {
        timerListeners.add(timerListener);
    }
    //  Sets the refresh board listener (which is actually the GameActivity).
    public void setRefreshBoardListener(RefreshBoardListener refreshListener) {
        this.refreshListener = refreshListener;
    }
    //  Sets the end game listener (which is actually the GameActivity).
    public void setEndGameListener(EndGameListener endGameListener) {
        this.endGameListener = endGameListener;
    }

    //  Sets the flag change listener (which is actually the GameActivity).
    public void setMinesUpdateListener(MinesUpdateListener minesUpdateListener) {
        this.minesUpdateListener = minesUpdateListener;
        minesUpdateListener.minesUpdated();
    }

    public boolean isFlagged(int row, int col) {
        return logicBoard[row][col].isFlagged();
    }

    public void stopThread() {
        timerThread.interrupt();
    }

    public enum ScreenSide {
        Top, Bottom, Right, Left, Initial;
    }
}