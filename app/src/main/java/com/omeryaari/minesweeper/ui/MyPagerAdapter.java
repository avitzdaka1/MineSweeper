package com.omeryaari.minesweeper.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public static int NUM_OF_LEVELS = 3;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return LevelFragment.newInstance(LevelFragment.Level.Easy.ordinal());
            case 1:
                return LevelFragment.newInstance(LevelFragment.Level.Normal.ordinal());
            case 2:
                return LevelFragment.newInstance(LevelFragment.Level.Hard.ordinal());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_OF_LEVELS;
    }
}
