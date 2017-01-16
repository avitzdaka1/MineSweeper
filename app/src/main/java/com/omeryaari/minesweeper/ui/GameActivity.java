package com.omeryaari.minesweeper.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.renderscript.Sampler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.games.Game;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.EndGameListener;
import com.omeryaari.minesweeper.logic.MinesUpdateListener;
import com.omeryaari.minesweeper.logic.MotionHandler;
import com.omeryaari.minesweeper.service.AccelerometerService;
import com.omeryaari.minesweeper.service.GPSTrackerService;
import com.omeryaari.minesweeper.logic.Logic;
import com.omeryaari.minesweeper.logic.RefreshBoardListener;
import com.omeryaari.minesweeper.logic.TimerChangedListener;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements TimerChangedListener, RefreshBoardListener, EndGameListener, MinesUpdateListener {

    private static final int TAG_CODE_PERMISSION_LOCATION = 1;
    private static final String TAG = GameActivity.class.getSimpleName();
    private TextView timeText;
    private TextView minesLeftText;
    private ImageButton selectionButton;
    private Logic gameLogic;
    private ImageButton[][] gameButtons;
    private GridLayout boardGrid;
    private int clickType;
    private int buttonSizeParam;
    private int gameSize;
    private int difficulty;
    private GPSTrackerService gpsTrackerService;
    private AccelerometerService accelerometerService;
    private DisplayMetrics metrics;

    //  Made up location to be used when the user doesn't allow location services to get location.
    public enum MadeUpLocation {
        Latitude(37.441082), Longitude(-122.141540);

        private double value;
        MadeUpLocation(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            if (binder instanceof GPSTrackerService.GPSServiceBinder)
                setGpsTrackerService(((GPSTrackerService.GPSServiceBinder) binder).getService());
            else if (binder instanceof AccelerometerService.AccelerometerServiceBinder)
                setAccelerometerService(((AccelerometerService.AccelerometerServiceBinder) binder).getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle b = getIntent().getExtras();
        this.difficulty = b.getInt("key");
        checkLocationPermissions();
        startAccelerometerService();
        gameLogic = new Logic(difficulty);
        gameLogic.setTimerListener(this);
        gameLogic.setRefreshBoardListener(this);
        gameLogic.setEndGameListener(this);
        gameLogic.setMinesUpdateListener(this);
        gameSize = gameLogic.getSize();
        timeText = (TextView) findViewById(R.id.time_text_view2);
        calcScreenSize();
        buttonSizeParam = metrics.widthPixels / (gameSize+1);
        createUIBoard();
        createSelectionButton();
    }

    //  Starts the accelerometer service.
    private void startAccelerometerService() {
        bindService(new Intent(GameActivity.this, AccelerometerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //  Checks for location services permissions, if there aren't any, function will ask the user to give permissions.
    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }
        else {
            if (gpsTrackerService == null)
                bindService(new Intent(GameActivity.this, GPSTrackerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            startListeningToGps();
        }
    }

    private void startListeningToGps() {
        if (gpsTrackerService != null)
            gpsTrackerService.startListening();
    }

    private void stopListeningToGps() {
        if (gpsTrackerService != null)
            gpsTrackerService.stopListening();
    }

    //  Checks the user's choice (whether to provide or to deny location permissions).
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case TAG_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (gpsTrackerService == null)
                        bindService(new Intent(GameActivity.this, GPSTrackerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
                    startListeningToGps();
                }
        }
    }

    private void setGpsTrackerService(GPSTrackerService gpsTrackerService) {
        if (gpsTrackerService != null)
            this.gpsTrackerService = gpsTrackerService;
        startListeningToGps();
    }

    private void setAccelerometerService(AccelerometerService accelerometerService) {
        if (accelerometerService != null) {
            this.accelerometerService = accelerometerService;
            MotionHandler motionHandler = new MotionHandler(accelerometerService, gameLogic);
            Thread motionThread = new Thread(motionHandler);
            motionThread.start();
            accelerometerService.setListener(motionHandler);
            gameLogic.setTimerListener(motionHandler);
        }
    }

    private void startListeningToAccelerometer() {
        if (accelerometerService != null)
            accelerometerService.startListening();
    }

    private void stopListeningToAccelerometer() {
        if (accelerometerService != null)
            accelerometerService.stopListening();
    }

    //  Creates the selection button, the button that allows the player to switch between placing
    //  a Mine and placing a Flag.
    private void createSelectionButton() {
        selectionButton = (ImageButton) findViewById(R.id.selection_button);
        int selectionButtonSize = metrics.widthPixels / 5;
        LinearLayout.LayoutParams selectionButtonParams = new LinearLayout.LayoutParams(selectionButtonSize, selectionButtonSize);
        selectionButtonParams.gravity = Gravity.CENTER;
        selectionButton.setLayoutParams(selectionButtonParams);
        clickType = Logic.CLICK_TYPE_MINE;
        selectionButton.setBackgroundResource(R.drawable.mine3_small);
        selectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickType == Logic.CLICK_TYPE_MINE) {
                    clickType = Logic.CLICK_TYPE_FLAG;
                    gameLogic.setClickType(Logic.CLICK_TYPE_FLAG);
                    selectionButton.setBackgroundResource(R.drawable.redflag);
                }
                else {
                    clickType = Logic.CLICK_TYPE_MINE;
                    gameLogic.setClickType(Logic.CLICK_TYPE_MINE);
                    selectionButton.setBackgroundResource(R.drawable.mine3_small);
                }
            }
        });
    }

    //  Creates the image buttons that represent the tiles.
    private void createImageButtons() {
        for (int row = 0; row < gameButtons.length; row++) {
            for (int col = 0; col < gameButtons[0].length; col++) {
                GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
                buttonParams.setGravity(Gravity.CENTER);
                buttonParams.width = buttonSizeParam;
                buttonParams.height = buttonSizeParam;
                ImageButton tempButton = new ImageButton(this);
                tempButton.setLayoutParams(buttonParams);
                tempButton.setOnClickListener(new MyOnClickListener(row, col, gameLogic));
                gameButtons[row][col] = tempButton;
                boardGrid.addView(tempButton);
            }
        }
    }

    //  Creates the UI board.
    private void createUIBoard() {
        boardGrid = (GridLayout) findViewById(R.id.board_grid);
        boardGrid.setColumnCount(gameSize);
        boardGrid.setRowCount(gameSize);
        gameButtons = new ImageButton[gameSize][gameSize];
        createImageButtons();
    }

    @Override
    protected void onPause() {
        stopListeningToGps();
        stopListeningToAccelerometer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkLocationPermissions();
        startListeningToAccelerometer();
        super.onResume();
    }

    //  Acquires device's screen resolution.
    private void calcScreenSize() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    //  GameActivity runs this function when a time changed event occurred.
    //  Runs every second in order to update the timer text.
    @Override
    public void timeChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameLogic.getMinutes() < 10) {
                    if (gameLogic.getSeconds() < 10)
                        timeText.setText("0" + gameLogic.getMinutes() + ":0" + gameLogic.getSeconds());
                    else
                        timeText.setText("0" + gameLogic.getMinutes() + ":" + gameLogic.getSeconds());
                }
                else {
                    if (gameLogic.getSeconds() < 10)
                        timeText.setText(gameLogic.getMinutes() + ":0" + gameLogic.getSeconds());
                    else
                        timeText.setText(gameLogic.getMinutes() + ":" + gameLogic.getSeconds());
                }
            }
        });
    }

    //  GameActivity runs this function when a refresh board event occurred.
    //  Runs when an empty tile has been clicked.
    @Override
    public void refreshBoard() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int[][] gameIntBoard = gameLogic.getIntBoard();
                for(int row = 0; row < gameIntBoard.length; row++) {
                    for(int col = 0; col < gameIntBoard.length; col++) {
                        if (gameIntBoard[row][col] != Logic.TILE_INVISIBLE) {
                            gameButtons[row][col].callOnClick();
                        }
                        else if (!gameLogic.isFlagged(row, col)) {
                            gameButtons[row][col].setImageResource(android.R.color.transparent);
                        }
                    }
                }
            }
        });
    }

    //  GameActivity runs this function when an end game event occurred.
    //  Runs when game has ended.
    @Override
    public void onEndGame(final int outcome) {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                gameLogic.stopThread();
                selectionButton.setVisibility(View.INVISIBLE);
                final ImageView animImageView = (ImageView) findViewById(R.id.animation_image_view);
                FrameLayout frame = (FrameLayout) findViewById(R.id.frame_layout);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frame.getWidth(), frame.getHeight());
                animImageView.setLayoutParams(params);
                if (outcome == Logic.OUTCOME_LOSS) {
                    animImageView.setBackgroundResource(R.drawable.animation_explosion);
                    AnimationDrawable lossAnimation = (AnimationDrawable) animImageView.getBackground();
                    lossAnimation.start();
                    for(int i = 0; i < gameButtons.length; i++) {
                        for(int j = 0; j < gameButtons[0].length; j++) {
                            animateRandomlyFlyingOut(gameButtons[i][j], 1400);
                        }
                    }
                } else {
                    boardGrid.setVisibility(View.INVISIBLE);
                    animImageView.setBackgroundResource(R.drawable.winner_cup);
                    ObjectAnimator winAnimation = ObjectAnimator.ofFloat(animImageView, "alpha", 0, 1);
                    winAnimation.setDuration(1400);
                    winAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                    winAnimation.start();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(GameActivity.this, OutcomeActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("outcome", outcome);
                        b.putInt("minutes", gameLogic.getMinutes());
                        b.putInt("seconds", gameLogic.getSeconds());
                        b.putInt("difficulty", difficulty);
                        Location currentLocation = gpsTrackerService.getLocation();
                        if (currentLocation != null) {
                            b.putDouble("latitude", currentLocation.getLatitude());
                            b.putDouble("longitude", currentLocation.getLongitude());
                        } else {
                            b.putDouble("latitude", MadeUpLocation.Latitude.getValue());
                            b.putDouble("longitude", MadeUpLocation.Longitude.getValue());
                        }
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }
                }, 1400);
            }
        });
    }

    public void animateRandomlyFlyingOut(View view, long duration) {
        Random random = new Random();
        int otherSide;
        otherSide = random.nextBoolean() ? 1 : -1;
        ObjectAnimator flyOutX = ObjectAnimator.ofFloat(view, "x", view.getX(), metrics.widthPixels * otherSide);
        flyOutX.setDuration(duration);
        flyOutX.setInterpolator(new DecelerateInterpolator());

        otherSide = random.nextBoolean() ? 1 : -1;
        ObjectAnimator flyOutY = ObjectAnimator.ofFloat(view, "y", view.getY(), metrics.widthPixels * otherSide);
        flyOutY.setDuration(duration);
        flyOutY.setInterpolator(new DecelerateInterpolator());

        otherSide = random.nextBoolean() ? 1 : -1;
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", otherSide == 1 ? 0f : 360f, otherSide == 1 ? 360f : 0f);
        rotate.setDuration(duration);
        rotate.setInterpolator(new DecelerateInterpolator());

        rotate.start();
        flyOutY.start();
        flyOutX.start();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    //  GameActivity runs this function when a flag changed event occurred.
    //  Basically, this runs whenever a flag has been placed / unplaced.
    @Override
    public void minesUpdated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                minesLeftText = (TextView) findViewById(R.id.mines_left_text_view2);
                minesLeftText.setText(String.valueOf(gameLogic.getMinesLeft()));
            }
        });
    }
}