package com.omeryaari.minesweeper.ui;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.location.Location;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.logic.Highscore;
import com.omeryaari.minesweeper.logic.Logic;

import java.util.ArrayList;
import java.util.Collections;

public class OutcomeActivity extends AppCompatActivity implements LocationListener {

    private final String TAG = this.getClass().getSimpleName();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private TextView outcomeText;
    private Button saveButton;
    private EditText saveName;
    private int difficulty;
    private String level;
    private Highscore highscore;
    private ArrayList<Highscore> highscoreList = new ArrayList<>();
    private Location currentLocation;
    protected LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getLocation();
        Bundle b = getIntent().getExtras();
        int outcome = b.getInt("outcome");
        int minutes = b.getInt("minutes");
        int seconds = b.getInt("seconds");
        difficulty = b.getInt("difficulty");
        currentLocation = new Location("");
        currentLocation.setLatitude(b.getDouble("latitude"));
        currentLocation.setLongitude(b.getDouble("longitude"));
        outcomeText = (TextView) findViewById(R.id.outcome_text_view);
        saveButton = (Button) findViewById(R.id.save_score_button);
        outcomeText.setBackgroundResource(R.drawable.outcome_cell);
        loadScores();
        highscore = new Highscore();
        highscore.setMinutes(minutes);
        highscore.setSeconds(seconds);
        if (outcome == Logic.OUTCOME_WIN) {
            outcomeText.setText(R.string.win_text);
            if (isNewHighscore())
                winNewHighscoreCodeSequence();
            else
                winNoHighscoreCodeSequence();
        } else
            lossCodeSequence();
        setTimePlayed();
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                if (isNetworkEnabled) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (isGPSEnabled) {
                    if (currentLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null)
                            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //  Runs if the player has won and made a high score.
    private void winNewHighscoreCodeSequence() {
        LinearLayout saveNameLayout = (LinearLayout) findViewById(R.id.save_name_layout);
        saveName = new EditText(this);
        saveName.setHint(R.string.name_edit_text);
        saveName.setInputType(InputType.TYPE_CLASS_TEXT);
        saveNameLayout.addView(saveName);
        TextView highscoreText = (TextView) findViewById(R.id.new_highscore_text_view);
        highscoreText.setText(R.string.highscore_text);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highscore.setName(saveName.getText().toString());
                saveScore();
                OutcomeActivity.this.finish();
            }
        });
    }

    //  Runs if the player has won but hasn't made a high score.
    private void winNoHighscoreCodeSequence() {
        saveButton.setText(R.string.save_button_return);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutcomeActivity.this.finish();
            }
        });
    }

    //  Runs if the player lost the game.
    private void lossCodeSequence() {
        outcomeText.setText(R.string.loss_text);
        saveButton.setText(R.string.save_button_return);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutcomeActivity.this.finish();
            }
        });
    }

    //  Sets the time in the time played TextView to the time the player played.
    private void setTimePlayed() {
        TextView timePlayedText = (TextView) findViewById(R.id.time_played_text_view2);
        timePlayedText.setText(highscore.getCorrectedTimeString());
    }

    //  Saves score.
    private void saveScore() {
        DatabaseReference highscoresDB = FirebaseDatabase.getInstance().getReference();
        if (currentLocation != null) {
            highscore.setLatitude(currentLocation.getLatitude());
            highscore.setLongitude(currentLocation.getLongitude());
        }
        highscoreList.add(highscore);
        //  If this highscore isn't the first highscore.
        if (highscoreList.size() > Highscore.MAX_HIGHSCORES) {
            Collections.sort(highscoreList);
            //  Remove the last highscore that isn't a highscore anymore.
            highscoresDB.child("Highscores").child(level).child("List").child(highscoreList.get(highscoreList.size() - 1).getFirebaseKey()).removeValue();
            highscoreList.remove(highscoreList.size() - 1);
        }
        highscore.setFirebaseKey(highscoresDB.child("Highscores").child(level).child("List").push().getKey());
        highscoresDB.child("Highscores").child(level).child("List").child(highscore.getFirebaseKey()).setValue(highscore);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    public String determineLevel(int level) {
        switch (level) {
            case 0:
                return "Easy";
            case 1:
                return "Normal";
            case 2:
                return "Hard";
        }
        return "";
    }

    //  Loads high scores from firebase.
    private void loadScores() {
        level = determineLevel(difficulty);
        DatabaseReference highscoresDB = FirebaseDatabase.getInstance().getReference();
        highscoresDB.child("Highscores").child(level).child("List").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Highscore tempHighScore = snap.getValue(Highscore.class);
                    highscoreList.add(tempHighScore);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error loading scores from firebase (onCancalled was called).");
            }
        });
    }

    //  Checks if the current score is a highscore.
    private boolean isNewHighscore() {
        if (highscoreList.size() < Highscore.MAX_HIGHSCORES)
            return true;
        else
            return highscore.compareTo(highscoreList.get(highscoreList.size() - 1)) == 1;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}