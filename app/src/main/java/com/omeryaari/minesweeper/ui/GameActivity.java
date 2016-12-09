package com.omeryaari.minesweeper.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.Logic;
import com.omeryaari.minesweeper.logic.RefreshBoardListener;
import com.omeryaari.minesweeper.logic.TimerChangedListener;

public class GameActivity extends AppCompatActivity implements TimerChangedListener, RefreshBoardListener{
    public static final int CLICK_TYPE_MINE = 0;
    public static final int CLICK_TYPE_FLAG = 1;
    public static final int UI_DO_NOTHING = -1;
    private final String TAG = GameActivity.class.getSimpleName();
    private TextView timeText;
    private TextView minesLeftText;
    private ImageButton selectionButton;
    private Logic gameLogic;
    private ImageButton[][] gameButtons;
    private GridLayout boardGrid;
    private int clickType;
    private int buttonSizeParam;
    private int gameSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle b = getIntent().getExtras();
        int difficulty = b.getInt("key");
        gameLogic = new Logic(difficulty);
        gameLogic.setTimerListener(this);
        gameLogic.setRefreshBoardListener(this);
        gameSize = gameLogic.getSize();
        timeText = (TextView) findViewById(R.id.time_text_view2);
        calcButtonSize();
        createUIBoard();
        createSelectionButton();
    }

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

    private void drawBoard() {
        int[][] intBoard = gameLogic.getIntBoard();
    }

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

    @Override
    public void refreshBoard() {
        int[][] gameIntBoard = gameLogic.getIntBoard();
        for(int row = 0; row < gameIntBoard.length; row++) {
            for(int col = 0; col < gameIntBoard.length; col++) {
                if (gameIntBoard[row][col] != UI_DO_NOTHING) {
                    gameButtons[row][col].callOnClick();
                    Log.d(TAG, "called click on row " + row + " and col " + col);
                }
                Log.d(TAG, "ui did nothing!");
            }
        }
    }
}
