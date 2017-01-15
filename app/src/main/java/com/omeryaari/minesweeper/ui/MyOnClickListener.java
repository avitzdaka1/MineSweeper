package com.omeryaari.minesweeper.ui;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;

import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.Logic;
import com.omeryaari.minesweeper.logic.Tile;

public class MyOnClickListener implements View.OnClickListener {

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
        Tile.TileProperty tempTileProperty = Tile.TileProperty.Invisible;
        for(Tile.TileProperty tileProp : Tile.TileProperty.values())
            if(value == tileProp.ordinal())
                tempTileProperty = tileProp;
        switch(tempTileProperty) {
            case Empty:
                thisButton.setImageResource(android.R.color.black);
                break;
            case Flag:
                thisButton.setImageResource(R.drawable.redflag);
                break;
            case Mine:
                thisButton.setImageResource(R.drawable.mine3_small);
                break;
            case One:
                thisButton.setImageResource(R.drawable.one);
                break;
            case Two:
                thisButton.setImageResource(R.drawable.two);
                break;
            case Three:
                thisButton.setImageResource(R.drawable.three);
                break;
            case Four:
                thisButton.setImageResource(R.drawable.four);
                break;
            case Five:
                thisButton.setImageResource(R.drawable.five);
                break;
            case Six:
                thisButton.setImageResource(R.drawable.six);
                break;
            case Seven:
                thisButton.setImageResource(R.drawable.seven);
                break;
            case Eight:
                thisButton.setImageResource(R.drawable.eight);
                break;
            default:
                thisButton.setImageResource(android.R.color.transparent);
                break;
        }
    }
}
