package com.omeryaari.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.omeryaari.minesweeper.R;

public class LevelFragment extends Fragment {

    public static final int TEMPLATE_NAME = 0;
    public static final int TEMPLATE_TIME = 1;
    public static final int HIGHSCORE_NAME = 2;
    public static final int HIGHSCORE_TIME = 3;
    public static final int NO_SCORE = -1;
    public static final int HIGHSCORE_TEXT_SIZE = 15;
    public static final int HIGHSCORE_TABLE_SIZE = 4;
    private int position;
    private int highScore;
    private String title;
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
        position = getArguments().getInt("position");
        title = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_level, container, false);
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
        Button highscoresButton = (Button) view.findViewById(R.id.level_fragment_highscores_button);
        highscoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HighscoreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("key", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
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
