package com.omeryaari.minesweeper.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.omeryaari.minesweeper.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        ImageButton helpImage = (ImageButton) findViewById(R.id.help_image);
        helpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startHelpActivity();
            }
        });
    }

    private void startHelpActivity() {
        Intent intent = new Intent(MenuActivity.this, HelpActivity.class);
        MenuActivity.this.startActivity(intent);
    }
}
