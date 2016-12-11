package com.omeryaari.minesweeper.logic;

//  A listener used to tell the GameActivity to refresh the board (which means,
//  to go over all of the tiles and unveil them if they're visible in the gameLogic).
public interface RefreshBoardListener {
    void refreshBoard();
}
