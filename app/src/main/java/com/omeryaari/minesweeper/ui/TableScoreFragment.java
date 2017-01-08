package com.omeryaari.minesweeper.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.Highscore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;

public class TableScoreFragment extends Fragment {

    private GridLayout tableGrid;
    private ArrayList<Highscore> highscoreList;

    public void setHighscoreList(ArrayList<Highscore> highscoreList) {
        this.highscoreList = highscoreList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        tableGrid = (GridLayout) mainView.findViewById(R.id.table_gridlayout);
        if (highscoreList != null)
            initTable();
        ScrollView scrollView = (ScrollView) mainView.findViewById(R.id.table_scrollview);
        scrollView.setVerticalScrollBarEnabled(true);
        scrollView.setHorizontalScrollBarEnabled(false);
        return mainView;
    }

    public static TableScoreFragment newInstance(ArrayList<Highscore> highscoreList) {
        TableScoreFragment fragment = new TableScoreFragment();
        fragment.setHighscoreList(highscoreList);
        return fragment;
    }

    private void initTable() {
        tableGrid.setColumnCount(Highscore.NUM_PROPS);
        tableGrid.setRowCount(highscoreList.size() + 1);
        for(int i = 0; i < highscoreList.size() + 1; i++) {
            for (int j = 0; j < tableGrid.getColumnCount(); j++) {
                TextView tempTextView = new TextView(getActivity().getApplicationContext());
                if (i == 0) {
                    switch (j) {
                        case 0:
                            tempTextView.setText(R.string.highscores_hashtag_textview);
                            break;
                        case 1:
                            tempTextView.setText(R.string.highscores_name_textview);
                            break;
                        case 2:
                            tempTextView.setText(R.string.highscores_time_textview);
                            break;
                        case 3:
                            tempTextView.setText(R.string.highscores_location_textview);
                            break;
                    }
                }
                else {
                    switch (j) {
                        case 0:
                            tempTextView.setText(String.valueOf(i-1));
                            break;
                        case 1:
                            tempTextView.setText(highscoreList.get(i-1).getName());
                            break;
                        case 2:
                            tempTextView.setText(highscoreList.get(i-1).getCorrectedTimeString());
                            break;
                        case 3:
                            int longitude = highscoreList.get(i-1).getLongitude();
                            int latitude = highscoreList.get(i-1).getLatitude();
                            tempTextView.setText(longitude + ", " + latitude);
                            break;
                    }
                }
                LinearLayout tempLayout = new LinearLayout(getActivity().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                tempTextView.setTextColor(Color.BLACK);
                tempTextView.setTextSize(20);
                tempTextView.setLayoutParams(params);
                //tempTextView.setGravity(Gravity.CENTER);
                tempTextView.setBackgroundResource(R.drawable.cell_shape);
                tempLayout.addView(tempTextView);
                tableGrid.addView(tempLayout);
            }
        }
    }
}