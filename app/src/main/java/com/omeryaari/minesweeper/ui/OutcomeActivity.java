package com.omeryaari.minesweeper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;

public class OutcomeActivity extends AppCompatActivity {
    private ImageView outcomeImage;
    private ImageView newHighscore;
    private TextView timePlayedText;
    private TextView bestTimeText;
    private ImageButton newGameImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcome);
    }

    public void switchToMenu() {

    }
}
