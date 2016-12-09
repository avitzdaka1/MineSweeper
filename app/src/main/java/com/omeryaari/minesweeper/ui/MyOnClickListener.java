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
        thisButton.setPadding(10,10,10,10);
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
            case 1: {
                thisButton.setImageResource(R.drawable.one);
                break;
            }
            case 2: {
                thisButton.setImageResource(R.drawable.two);
                break;
            }
            case 3: {
                thisButton.setImageResource(R.drawable.three);
                break;
            }
            case 4: {
                thisButton.setImageResource(R.drawable.four);
                break;
            }
            case 5: {
                thisButton.setImageResource(R.drawable.five);
                break;
            }
            case 6: {
                thisButton.setImageResource(R.drawable.six);
                break;
            }
            case 7: {
                thisButton.setImageResource(R.drawable.seven);
                break;
            }
            case 8: {
                thisButton.setImageResource(R.drawable.eight);
                break;
            }
            default:
                thisButton.setImageResource(android.R.color.transparent);
                break;
        }
    }
}
