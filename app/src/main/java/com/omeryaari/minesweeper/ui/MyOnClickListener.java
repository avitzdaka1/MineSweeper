package com.omeryaari.minesweeper.ui;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.Logic;

public class MyOnClickListener implements View.OnClickListener {
    public static final int TILE_EMPTY = 0;
    public static final int TILE_FLAG = 9;
    public static final int TILE_MINE = 10;
    public static final int TILE_ONE = 1;
    public static final int TILE_TWO = 2;
    public static final int TILE_THREE = 3;
    public static final int TILE_FOUR = 4;
    public static final int TILE_FIVE = 5;
    public static final int TILE_SIX = 6;
    public static final int TILE_SEVEN = 7;
    public static final int TILE_EIGHT = 8;
    public static final int TILE_DEFAULT_PADDING = 10;
    private int row;
    private int col;
    private Logic gameLogic;

    public MyOnClickListener(int row, int col, Logic gameLogic) {
        this.row = row;
        this.col = col;
        this.gameLogic = gameLogic;
    }

    @Override
    public void onClick(View v) {
        ImageButton thisButton = (ImageButton) v;
        thisButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        thisButton.setPadding(TILE_DEFAULT_PADDING, TILE_DEFAULT_PADDING, TILE_DEFAULT_PADDING, TILE_DEFAULT_PADDING);
        int value = gameLogic.checkTile(row, col);
        switch(value) {
            case TILE_EMPTY: {
                thisButton.setImageResource(android.R.drawable.screen_background_light);
                break;
            }
            case TILE_FLAG: {
                thisButton.setImageResource(R.drawable.redflag);
                break;
            }
            case TILE_MINE: {
                thisButton.setImageResource(R.drawable.mine);
                break;
            }
            case TILE_ONE: {
                thisButton.setImageResource(R.drawable.one);
                break;
            }
            case TILE_TWO: {
                thisButton.setImageResource(R.drawable.two);
                break;
            }
            case TILE_THREE: {
                thisButton.setImageResource(R.drawable.three);
                break;
            }
            case TILE_FOUR: {
                thisButton.setImageResource(R.drawable.four);
                break;
            }
            case TILE_FIVE: {
                thisButton.setImageResource(R.drawable.five);
                break;
            }
            case TILE_SIX: {
                thisButton.setImageResource(R.drawable.six);
                break;
            }
            case TILE_SEVEN: {
                thisButton.setImageResource(R.drawable.seven);
                break;
            }
            case TILE_EIGHT: {
                thisButton.setImageResource(R.drawable.eight);
                break;
            }
            default:
                thisButton.setImageResource(android.R.color.transparent);
                break;
        }
    }
}
