package com.omeryaari.minesweeper.ui;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;

public class OutcomeActivity extends AppCompatActivity {

    public static final int OUTCOME_WIN = 1;
    private TextView outcomeText;
    private Button saveButton;
    private EditText saveName;
    private int minutes;
    private int seconds;
    private int difficulty;
    String highscoreName;
    int highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle b = getIntent().getExtras();
        int outcome = b.getInt("outcome");
        minutes = b.getInt("minutes");
        seconds = b.getInt("seconds");
        difficulty = b.getInt("difficulty");
        outcomeText = (TextView) findViewById(R.id.outcome_text_view);
        saveButton = (Button) findViewById(R.id.save_score_button);
        outcomeText.setBackgroundResource(R.drawable.outcome_cell);
        loadScore();
        if (outcome == OUTCOME_WIN) {
            outcomeText.setText(R.string.win_text);
            if (checkHighScore(highscore, minutes, seconds))
                winNewHighscoreCodeSequence();
            else
                winNoHighscoreCodeSequence();
        }
        else
            lossCodeSequence();
        setBestTime();
        setTimePlayed();
    }

    //  Runs if the player has won and made a high score.
    private void winNewHighscoreCodeSequence() {
        highscore = (minutes * 100) + seconds;
        LinearLayout saveNameLayout = (LinearLayout)findViewById(R.id.save_name_layout);
        saveName = new EditText(this);
        saveName.setHint(R.string.name_edit_text);
        saveName.setInputType(InputType.TYPE_CLASS_TEXT);
        saveNameLayout.addView(saveName);
        TextView highscoreText = (TextView) findViewById(R.id.new_highscore_text_view);
        highscoreText.setText(R.string.highscore_text);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highscoreName = saveName.getText().toString();
                saveScore();
                OutcomeActivity.this.finish();
            }
        });
    }

    //  Runs if the player has won but hasn't made a high score.
    private void winNoHighscoreCodeSequence() {
        saveButton.setText(R.string.save_button_return);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutcomeActivity.this.finish();
            }
        });
    }

    //  Runs if the player lost the game.
    private void lossCodeSequence() {
        outcomeText.setText(R.string.loss_text);
        saveButton.setText(R.string.save_button_return);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutcomeActivity.this.finish();
            }
        });
    }

    //  Sets the time in the best time text view to the highscore time.
    private void setBestTime() {
        TextView bestTimeText = (TextView) findViewById(R.id.best_time_text_view2);
        bestTimeText.setText(getCorrectedTimeString(highscore / 100, highscore % 100));
        if (highscore == -1) {
            TextView bestTime = (TextView) findViewById(R.id.best_time_text_view1);
            bestTime.setText("");
            bestTimeText.setText("");
        }
    }

    //  Sets the time in the time played TextView to the time the player played.
    private void setTimePlayed() {
        TextView timePlayedText = (TextView) findViewById(R.id.time_played_text_view2);
        timePlayedText.setText(getCorrectedTimeString(minutes, seconds));
    }

    //  Saves score.
    private void saveScore() {
        SharedPreferences scoresPref = getSharedPreferences("scores", MODE_PRIVATE);
        SharedPreferences.Editor scoresPrefEditor = scoresPref.edit();
        scoresPrefEditor.putString("player" + difficulty + "name", highscoreName);
        scoresPrefEditor.putInt("player" + difficulty + "score", highscore);
        scoresPrefEditor.apply();
    }

    //  Loads current level's score.
    private void loadScore() {
        SharedPreferences scoresPref = getSharedPreferences("scores", MODE_PRIVATE);
        highscoreName = scoresPref.getString("player" + difficulty + "name", null);
        highscore = scoresPref.getInt("player" + difficulty + "score", -1);
    }

    //  Generates "time strings"
    private String getCorrectedTimeString(int minutes, int seconds) {
        String time;
        if (minutes < 10) {
            if (seconds < 10)
                time = ("0" + minutes + ":0" + seconds);
            else
                time = ("0" + minutes + ":" + seconds);
        }
        else {
            if (seconds < 10)
                time = (minutes + ":0" + seconds);
            else
                time = (minutes + ":" + seconds);
        }
        return time;
    }

    //  Checks if current score is a high score.
    private boolean checkHighScore(int highScore, int minutes, int seconds) {
        if (highScore == -1)
            return true;
        int highScoreMinutes = highScore / 100;
        int highScoreSeconds = highScore % 100;
        if (minutes < highScoreMinutes)
            return true;
        else if (minutes == highScoreMinutes && seconds < highScoreSeconds)
            return true;
        else
            return false;
    }
}