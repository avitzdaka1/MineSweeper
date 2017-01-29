package com.omeryaari.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.omeryaari.minesweeper.R;

public class LevelFragment extends Fragment {

    public static final int ARROW_ANIMATION_DURATION = 1000;
    private int position;

    public enum Level {
        Easy("Easy"), Normal("Normal"), Hard("Hard");

        private String value;

        Level(String value) { this.value = value; }

        public String getValue() { return value; }
    }

    public static LevelFragment newInstance(int position) {
        LevelFragment fragment = new LevelFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_level, container, false);
        ImageView levelImageView = (ImageView) view.findViewById(R.id.level_image_view) ;
        Level level = Level.Easy;
        for(Level tempLevel : Level.values())
            if (position == tempLevel.ordinal())
                level = tempLevel;
        switch(level) {
            case Easy:
                levelImageView.setBackgroundResource(R.drawable.level_easy);
                break;
            case Normal:
                levelImageView.setBackgroundResource(R.drawable.level_normal);
                break;
            case Hard:
                levelImageView.setBackgroundResource(R.drawable.level_hard);
                break;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(metrics.widthPixels / 6, metrics.widthPixels / 6);
        ImageView leftArrow = (ImageView) view.findViewById(R.id.left_arrow_image);
        ImageView rightArrow = (ImageView) view.findViewById(R.id.right_arrow_image);
        leftArrow.setLayoutParams(params);
        rightArrow.setLayoutParams(params);
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(ARROW_ANIMATION_DURATION);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        switch(position) {
            case 0:
                rightArrow.setBackgroundResource(R.drawable.red_arrow_right);
                rightArrow.startAnimation(animation);
                break;
            case 1:
                rightArrow.setBackgroundResource(R.drawable.red_arrow_right);
                leftArrow.setBackgroundResource(R.drawable.red_arrow_left);
                rightArrow.startAnimation(animation);
                leftArrow.startAnimation(animation);
                break;
            case 2:
                leftArrow.setBackgroundResource(R.drawable.red_arrow_left);
                leftArrow.startAnimation(animation);
                break;
        }
        ImageButton gameStart = (ImageButton) view.findViewById(R.id.play_image_button);
        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("difficulty", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageButton highscoresButton = (ImageButton) view.findViewById(R.id.level_fragment_highscores_button);
        highscoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HighscoreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("difficulty", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }
}
