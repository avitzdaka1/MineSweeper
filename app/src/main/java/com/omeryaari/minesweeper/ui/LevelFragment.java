package com.omeryaari.minesweeper.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.omeryaari.minesweeper.R;

public class LevelFragment extends Fragment {

    private int position;
    private String title;

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
        ImageView levelImageView = (ImageView) view.findViewById(R.id.level_image_view) ;
        switch(title) {
            case "Easy":
                levelImageView.setBackgroundResource(R.drawable.level_easy);
                break;
            case "Normal":
                levelImageView.setBackgroundResource(R.drawable.level_normal);
                break;
            case "Hard":
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
        animation.setDuration(1000);
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
        ImageButton highscoresButton = (ImageButton) view.findViewById(R.id.level_fragment_highscores_button);
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
}
