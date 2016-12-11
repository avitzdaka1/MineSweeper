package com.omeryaari.minesweeper.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.EndGameListener;
import com.omeryaari.minesweeper.logic.FlagChangeListener;
import com.omeryaari.minesweeper.logic.Logic;
import com.omeryaari.minesweeper.logic.RefreshBoardListener;
import com.omeryaari.minesweeper.logic.TimerChangedListener;

public class GameActivity extends AppCompatActivity implements TimerChangedListener, RefreshBoardListener, EndGameListener, FlagChangeListener{
    public static final int CLICK_TYPE_MINE = 0;
    public static final int CLICK_TYPE_FLAG = 1;
    public static final int UI_DO_NOTHING = -1;
    private TextView timeText;
    private TextView minesLeftText;
    private ImageButton selectionButton;
    private Logic gameLogic;
    private ImageButton[][] gameButtons;
    private GridLayout boardGrid;
    private int clickType;
    private int buttonSizeParam;
    private int gameSize;
    private int difficulty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle b = getIntent().getExtras();
        this.difficulty = b.getInt("key");
        gameLogic = new Logic(difficulty);
        gameLogic.setTimerListener(this);
        gameLogic.setRefreshBoardListener(this);
        gameLogic.setEndGameListener(this);
        gameLogic.setFlagChangeListener(this);
        gameSize = gameLogic.getSize();
        timeText = (TextView) findViewById(R.id.time_text_view2);
        calcButtonSize();
        createUIBoard();
        createSelectionButton();
    }

    //  Creates the selection button, the button that allows the player to switch between placing
    //  a Mine and placing a Flag.
    private void createSelectionButton() {
        selectionButton = (ImageButton) findViewById(R.id.selection_button);
        LinearLayout.LayoutParams selectionButtonParams = new LinearLayout.LayoutParams(buttonSizeParam*2, buttonSizeParam*2);
        selectionButtonParams.gravity = Gravity.CENTER;
        selectionButton.setLayoutParams(selectionButtonParams);
        clickType = CLICK_TYPE_MINE;
        selectionButton.setBackgroundResource(R.drawable.mine);
        selectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickType == CLICK_TYPE_MINE) {
                    clickType = CLICK_TYPE_FLAG;
                    gameLogic.setClickType(CLICK_TYPE_FLAG);
                    selectionButton.setBackgroundResource(R.drawable.redflag);
                }
                else {
                    clickType = CLICK_TYPE_MINE;
                    gameLogic.setClickType(CLICK_TYPE_MINE);
                    selectionButton.setBackgroundResource(R.drawable.mine);
                }
            }
        });
    }

    //  Creates the image buttons that represent the tiles.
    private void createImageButtons() {
        for (int row = 0; row < gameButtons.length; row++) {
            for (int col = 0; col < gameButtons[0].length; col++) {
                GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
                buttonParams.setGravity(Gravity.CENTER);
                buttonParams.width = buttonSizeParam;
                buttonParams.height = buttonSizeParam;
                ImageButton tempButton = new ImageButton(this);
                tempButton.setLayoutParams(buttonParams);
                tempButton.setOnClickListener(new MyOnClickListener(row, col, gameLogic));
                gameButtons[row][col] = tempButton;
                boardGrid.addView(tempButton);
            }
        }
    }

    //  Creates the UI board.
    private void createUIBoard() {
        boardGrid = (GridLayout) findViewById(R.id.board_grid);
        boardGrid.setColumnCount(gameSize);
        boardGrid.setRowCount(gameSize);
        gameButtons = new ImageButton[gameSize][gameSize];
        createImageButtons();
    }

    //  Acquires device's screen resolution.
    private void calcButtonSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        buttonSizeParam = metrics.widthPixels / (gameSize+1);
    }

    //  GameActivity runs this function when a time changed event occurred.
    //  Runs every second in order to update the timer text.
    @Override
    public void timeChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameLogic.getMinutes() < 10) {
                    if (gameLogic.getSeconds() < 10)
                        timeText.setText("0" + gameLogic.getMinutes() + ":0" + gameLogic.getSeconds());
                    else
                        timeText.setText("0" + gameLogic.getMinutes() + ":" + gameLogic.getSeconds());
                }
                else {
                    if (gameLogic.getSeconds() < 10)
                        timeText.setText(gameLogic.getMinutes() + ":0" + gameLogic.getSeconds());
                    else
                        timeText.setText(gameLogic.getMinutes() + ":" + gameLogic.getSeconds());
                }
            }
        });
    }

    //  GameActivity runs this function when a refresh board event occurred.
    //  Runs when an empty tile has been clicked.
    @Override
    public void refreshBoard() {
        int[][] gameIntBoard = gameLogic.getIntBoard();
        for(int row = 0; row < gameIntBoard.length; row++) {
            for(int col = 0; col < gameIntBoard.length; col++) {
                if (gameIntBoard[row][col] != UI_DO_NOTHING) {
                    gameButtons[row][col].callOnClick();
                }
            }
        }
    }

    //  GameActivity runs this function when an end game event occurred.
    //  Runs when game has ended.
    @Override
    public void onEndGame(int outcome) {
        Intent intent = new Intent(GameActivity.this, OutcomeActivity.class);
        Bundle b = new Bundle();
        b.putInt("outcome", outcome);
        b.putInt("minutes", gameLogic.getMinutes());
        b.putInt("seconds", gameLogic.getSeconds());
        b.putInt("difficulty", difficulty);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    //  GameActivity runs this function when a flag changed event occurred.
    //  Basically, this runs whenever a flag has been placed / unplaced.
    @Override
    public void flagChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                minesLeftText = (TextView) findViewById(R.id.mines_left_text_view2);
                minesLeftText.setText(String.valueOf(gameLogic.getMinesLeft()));
            }
        });
    }
}