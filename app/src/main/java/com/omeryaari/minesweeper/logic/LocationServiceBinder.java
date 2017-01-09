package com.omeryaari.minesweeper.logic;

import android.location.Location;
import android.os.Binder;

/**
 * Created by omer on 09/01/2017.
 */

public class LocationServiceBinder extends Binder {
    private static final String TAG = LocationServiceBinder.class.getSimpleName();
    GPSTrackerService gpsTrackerService;

    public void setService(GPSTrackerService gpsTrackerService) {
        this.gpsTrackerService = gpsTrackerService;
    }

    public Location getLocation() {
        return gpsTrackerService.getLocation();
    }
}
