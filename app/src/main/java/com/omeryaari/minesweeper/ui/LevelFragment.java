package com.omeryaari.minesweeper.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;


import static android.content.Context.MODE_PRIVATE;

public class LevelFragment extends Fragment {

    public static final int TEMPLATE_NAME = 0;
    public static final int TEMPLATE_TIME = 1;
    public static final int HIGHSCORE_NAME = 2;
    public static final int HIGHSCORE_TIME = 3;
    public static final int NO_SCORE = -1;
    public static final int HIGHSCORE_TEXT_SIZE = 15;
    public static final int HIGHSCORE_TABLE_SIZE = 4;
    private String title;
    private int position;
    private int highScore;
    private String playerName;

    public static LevelFragment newInstance(String title, int position) {
        LevelFragment fragment = new LevelFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position", 0);
        title = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.level_fragment, container, false);
        TextView levelText = (TextView) view.findViewById(R.id.levelTextXML);
        levelText.setText(title);
        ImageButton gameStart = (ImageButton) view.findViewById(R.id.playImageButton);
        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("key", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScores();
    }

    //  Updates the textviews that show the highscore in every fragment.
    public void updateScores() {
        loadScore();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int cellWidth = (metrics.widthPixels - (metrics.widthPixels / 5)) / 2;
        TextView[] textViews = new TextView[4];
        textViews[TEMPLATE_NAME] = (TextView) getView().findViewById(R.id.template_name);
        textViews[TEMPLATE_TIME] = (TextView) getView().findViewById(R.id.template_time);
        textViews[HIGHSCORE_NAME] = (TextView) getView().findViewById(R.id.highscore_name);
        textViews[HIGHSCORE_TIME] = (TextView) getView().findViewById(R.id.highscore_time);
        if (highScore != NO_SCORE) {
            fillTextViews(textViews, cellWidth);
        }
    }

    //  Loads current level's score.
    private void loadScore() {
        int difficulty = position;
        SharedPreferences scoresPref = this.getActivity().getSharedPreferences("scores", MODE_PRIVATE);
        playerName = scoresPref.getString("player" + difficulty + "name", null);
        highScore = scoresPref.getInt("player" + difficulty + "score", -1);
    }

    private void fillTextViews(TextView[] textViews, int cellWidth) {
        for(int i = 0; i < HIGHSCORE_TABLE_SIZE; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = cellWidth;
            params.height = cellWidth / 3;
            params.weight = 1;
            textViews[i].setLayoutParams(params);
            switch (i) {
                case TEMPLATE_NAME:
                    textViews[i].setText(getString(R.string.highScoresName));
                    break;
                case TEMPLATE_TIME:
                    textViews[i].setText(getString(R.string.highScoresTime));
                    break;
                case HIGHSCORE_NAME:
                    textViews[i].setText(playerName);
                    break;
                case HIGHSCORE_TIME:
                    generateTimeString(textViews[i]);
                    break;
            }
            textViews[i].setGravity(Gravity.CENTER);
            textViews[i].setTextSize(HIGHSCORE_TEXT_SIZE);
            textViews[i].setBackgroundResource(R.drawable.cell_shape);
        }
    }

    private void generateTimeString(TextView textView) {
        if (highScore < 10)
            textView.setText("00:0" + String.valueOf(highScore));
        else if (highScore < 100)
            textView.setText("00:" + String.valueOf(highScore));
        else if (highScore < 1000)
            textView.setText("0" + String.valueOf(highScore / 100) + ":" + String.valueOf(highScore % 100));
        else
            textView.setText(String.valueOf(highScore / 100) + ":" + String.valueOf(highScore % 100));
    }
}
