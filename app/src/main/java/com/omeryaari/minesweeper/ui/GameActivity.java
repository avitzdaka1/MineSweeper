package com.omeryaari.minesweeper.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.Logic;

public class GameActivity extends AppCompatActivity {

    private TextView timeText;
    private TextView minesLeftText;
    private ImageButton selectionButton;
    private Logic gameLogic;
    private ImageButton[][] gameButtons;
    private GridLayout boardGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle b = getIntent().getExtras();
        int difficulty = b.getInt("key");
        gameLogic = new Logic(difficulty);
        boardGrid = new GridLayout(this);
        boardGrid.setColumnCount(gameLogic.getSize());
        boardGrid.setRowCount(gameLogic.getSize());
        //boardGrid.set
        gameButtons = new ImageButton[gameLogic.getSize()][gameLogic.getSize()];
        createImageButtons();
        setContentView(boardGrid);
    }

    private void createImageButtons() {
        for (int row = 0; row < gameButtons.length; row++) {
            for (int col = 0; col < gameButtons[0].length; col++) {
                gameButtons[row][col] = new ImageButton(this);
                boardGrid.addView(gameButtons[row][col]);
            }
        }
    }

    private void drawBoard() {
        int[][] intBoard = gameLogic.getIntBoard();
    }
}
