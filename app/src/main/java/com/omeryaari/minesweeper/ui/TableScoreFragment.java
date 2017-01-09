package com.omeryaari.minesweeper.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.Highscore;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TableScoreFragment extends Fragment {

    private GridLayout tableGrid;
    private ArrayList<Highscore> highscoreList;
    private enum TableProps {
        NUM_OF_PROPS(3), TABLE_FIRST_LINE(0);
        private int value;

        TableProps(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public void setHighscoreList(ArrayList<Highscore> highscoreList) {
        this.highscoreList = highscoreList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        tableGrid = (GridLayout) mainView.findViewById(R.id.table_gridlayout);
        RelativeLayout tableRelativeLayout = (RelativeLayout) mainView.findViewById(R.id.table_relativelayout);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        if (highscoreList != null)
            initTable(width);
        return mainView;
    }

    public static TableScoreFragment newInstance(ArrayList<Highscore> highscoreList) {
        TableScoreFragment fragment = new TableScoreFragment();
        fragment.setHighscoreList(highscoreList);
        return fragment;
    }

    private void initTable(int width) {
        tableGrid.setColumnCount(TableProps.NUM_OF_PROPS.getValue());
        tableGrid.setRowCount(highscoreList.size() + 1);
        int cellSize = (width-150) / 5;
        for(int i = 0; i < highscoreList.size() + 1; i++) {
            for (int j = 0; j < tableGrid.getColumnCount(); j++) {
                TextView tempTextView = new TextView(getActivity().getApplicationContext());
                LinearLayout tempLayout = new LinearLayout(getActivity().getApplicationContext());
                LinearLayout.LayoutParams params;
                params = new LinearLayout.LayoutParams(cellSize, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (j == 1) {
                    params = new LinearLayout.LayoutParams(cellSize * 3, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
                if (i == TableProps.TABLE_FIRST_LINE.getValue()) {
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
                    }
                    tempTextView.setTypeface(null, Typeface.BOLD);
                }
                else {
                    switch (j) {
                        case 0:
                            tempTextView.setText(String.valueOf(i-1));
                            break;
                        case 1:
                            String tempName = highscoreList.get(i-1).getName();
                            if (tempName.length() > 15)
                                tempTextView.setText(tempName.substring(0, 15));
                            else
                                tempTextView.setText(tempName);
                            break;
                        case 2:
                            tempTextView.setText(highscoreList.get(i-1).getCorrectedTimeString());
                            break;
                    }
                }
                tempTextView.setTextColor(Color.BLACK);
                tempTextView.setTextSize(16);
                tempTextView.setLayoutParams(params);
                tempTextView.setGravity(Gravity.CENTER);
                tempTextView.setBackgroundResource(R.drawable.cell_shape);
                tempLayout.addView(tempTextView);
                tableGrid.addView(tempLayout);
            }
        }
    }
}