package com.omeryaari.minesweeper.ui;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omeryaari.minesweeper.logic.Highscore;
import com.omeryaari.minesweeper.R;
import com.omeryaari.minesweeper.service.AzimutService;
import com.omeryaari.minesweeper.service.GPSTrackerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HighscoreActivity extends AppCompatActivity implements AzimutService.AzimutListener, GPSTrackerService.LocationChangeListener{

    public static final String FIREBASE_LIST_LOCATION = "List";
    public static final String FIREBASE_HIGHSCORES_LOCATION = "Highscores";
    private static final int TAG_CODE_PERMISSION_LOCATION = 2;
    private static final float MAP_ZOOM_DEFAULT = 7.0f;
    private static final long MIN_TIME_BW_UPDATES = 500;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    private LevelFragment.Level level;
    private Fragment tableFragment;
    private ArrayList<Highscore> highscoreList = new ArrayList<>();
    private final String TAG = this.getClass().getSimpleName();
    private boolean isMap = false;
    private GoogleMap gMap;
    private Marker currentLocationMarker;
    private Location currentLocation;
    private AzimutService azimutService;
    private GPSTrackerService gpsTrackerService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            if (binder instanceof AzimutService.AzimutServiceBinder)
                setAzimutService(((AzimutService.AzimutServiceBinder) binder).getService());
            else if (binder instanceof GPSTrackerService.GPSServiceBinder)
                setGpsTrackerService(((GPSTrackerService.GPSServiceBinder) binder).getService());

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
        setContentView(R.layout.activity_highscore);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkLocationPermissions();
        startAzimutService();
        level = determineLevel(getIntent().getExtras().getInt("difficulty"));
        loadScores();
        ImageView highscoresLevel = (ImageView) findViewById(R.id.highscores_level_imageview);
        switch (level) {
            case Easy:
                highscoresLevel.setBackgroundResource(R.drawable.level_easy);
                break;
            case Normal:
                highscoresLevel.setBackgroundResource(R.drawable.level_normal);
                break;
            case Hard:
                highscoresLevel.setBackgroundResource(R.drawable.level_hard);
                break;
        }
        setupButtons();
    }

    private void startAzimutService() {
        bindService(new Intent(HighscoreActivity.this, AzimutService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

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
                bindService(new Intent(HighscoreActivity.this, GPSTrackerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            startListeningToGps();
        }
    }

    private void setGpsTrackerService(GPSTrackerService gpsTrackerService) {
        if (gpsTrackerService != null) {
            this.gpsTrackerService = gpsTrackerService;
            gpsTrackerService.setLocationChangeListener(this);
        }
        startListeningToGps();
    }

    private void setAzimutService(AzimutService azimutService) {
        if (azimutService != null) {
            this.azimutService = azimutService;
            azimutService.setListener(this);
        }
        startListeningToAzimut();
    }

    //  Determines level.
    public LevelFragment.Level determineLevel(int difficulty) {
        LevelFragment.Level level = LevelFragment.Level.Easy;
        for(LevelFragment.Level tempLevel : LevelFragment.Level.values())
            if (difficulty == tempLevel.ordinal())
                level = tempLevel;
        return level;
    }

    //  Sets the two buttons up and assigns listeners to them.
    private void setupButtons() {
        ImageButton tableButton = (ImageButton) findViewById(R.id.highscores_table_button);
        ImageButton mapButton = (ImageButton) findViewById(R.id.highscores_map_button);
        ImageButton returnButton = (ImageButton) findViewById(R.id.highscores_return_button);
        tableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMap)
                    showTable();
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMap)
                    showMap();
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //  Creates the table fragment on the activity's creation.
    private void createTableFragment() {
        tableFragment = TableScoreFragment.newInstance(highscoreList);
    }

    //  Loads the table fragment into the fragment place holder.
    private void showTable() {
        if (currentLocationMarker != null)
            currentLocationMarker.remove();
        isMap = false;
        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.highscores_fragment_placeholder, tableFragment);
        fragTrans.commit();
    }

    //  Loads the map fragment into the fragment place holder.
    private void showMap() {
        if (gpsTrackerService != null)
            currentLocation = gpsTrackerService.getLocation();
        startAzimutService();
        isMap = true;
        if (isGoogleMapsInstalled()) {
            // Add the Google Maps fragment dynamically
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            MapFragment mapFragment = MapFragment.newInstance();
            transaction.replace(R.id.highscores_fragment_placeholder, mapFragment);
            transaction.commit();

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), MAP_ZOOM_DEFAULT));
                    gMap = googleMap;
                    addCustomMarker();
                    for (int i = 0; i < highscoreList.size(); i++) {
                        Highscore tempScore = highscoreList.get(i);
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(tempScore.getLatitude(), tempScore.getLongitude(), 1);
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            }
                            else {
                                googleMap.addMarker(new MarkerOptions().
                                        position(new LatLng(tempScore.getLatitude(), tempScore.getLongitude())).
                                        title("Highscore #" + (i + 1)).
                                        snippet("Name: " + tempScore.getName() + "\n" + "Time: " + tempScore.getCorrectedTimeString() + "\n" + "Address: " + addresses.get(0).getAddressLine(0)));
                                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                    @Override
                                    public View getInfoWindow(Marker arg0) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {

                                        LinearLayout info = new LinearLayout(getApplicationContext());
                                        info.setOrientation(LinearLayout.VERTICAL);

                                        TextView title = new TextView(getApplicationContext());
                                        title.setTextColor(Color.BLACK);
                                        title.setGravity(Gravity.CENTER);
                                        title.setTypeface(null, Typeface.BOLD);
                                        title.setText(marker.getTitle());

                                        TextView snippet = new TextView(getApplicationContext());
                                        snippet.setTextColor(Color.GRAY);
                                        snippet.setText(marker.getSnippet());

                                        info.addView(title);
                                        info.addView(snippet);

                                        return info;
                                    }
                                });
                            }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                }
            });
        }
    }

    //  Creates a custom marker for the user's current location.
    private void addCustomMarker() {
        BitmapDescriptor currentLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.location_arrow_small);
        if (currentLocationMarker != null)
            currentLocationMarker.remove();
        if (currentLocation != null)
            currentLocationMarker = gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .rotation(0)
                    .icon(currentLocationIcon));
    }

    //  Loads the high scores from firebase.
    private void loadScores() {
        DatabaseReference highscoresDB = FirebaseDatabase.getInstance().getReference();
        highscoresDB.child(FIREBASE_HIGHSCORES_LOCATION).child(level.getValue()).child(FIREBASE_LIST_LOCATION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()) {
                    Highscore tempHighScore = snap.getValue(Highscore.class);
                    highscoreList.add(tempHighScore);
                }
                Collections.sort(highscoreList);
                createTableFragment();
                showTable();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error loading scores from firebase (onCancelled was called).");
            }
        });
    }

    //  Checks if google maps is installed.
    public boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return info != null;
        }
        catch(PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void startListeningToGps() {
        if (gpsTrackerService != null) {
            gpsTrackerService.setSettings(MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, false);
            gpsTrackerService.startListening();
        }
    }

    private void stopListeningToGps() {
        if (gpsTrackerService != null)
            gpsTrackerService.stopListening();
    }

    private void startListeningToAzimut() {
        if (azimutService != null)
            azimutService.startListening();
    }

    private void stopListeningToAzimut() {
        if (azimutService != null)
            azimutService.stopListening();
    }

    //  When the phone's orientation has changed (its actually more than orientation).
    @Override
    public void onRotationEvent(float rotation) {
        if (currentLocationMarker != null)
            currentLocationMarker.setRotation(rotation);
    }

    //  When the phone's location has changed.
    @Override
    public void onNewLocation(Location location) {
        if (currentLocationMarker != null)
            currentLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    protected void onResume() {
        checkLocationPermissions();
        startListeningToAzimut();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopListeningToGps();
        stopListeningToAzimut();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }


}
