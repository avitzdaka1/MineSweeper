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
        TextView text = (TextView)this.findViewById(R.id.fuck);
        text.setGravity(Gravity.CENTER);
        text.setTextSize(30);
        Bundle b = getIntent().getExtras();
        int level = b.getInt("key");
        switch (level) {
            case 0: {
                text.setText("Easy");
                break;
            }
            case 1: {
                text.setText("Normal");
                break;
            }
            case 2: {
                text.setText("Hard");
                break;
            }
        }
    }

    public void drawBoard() {

    }
}
