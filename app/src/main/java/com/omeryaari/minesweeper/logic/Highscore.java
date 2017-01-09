package com.omeryaari.minesweeper.logic;

import java.io.Serializable;

public class Highscore implements Serializable, Comparable<Highscore> {
    public static final int MAX_HIGHSCORES = 10;
    private String name;
    private String firebaseKey;
    private int minutes;
    private int seconds;
    private double longitude;
    private double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    @Override
    public int compareTo(Highscore highscore) {
        if (this.getMinutes() > highscore.getMinutes())
            return 1;
        else if (this.getMinutes() == highscore.getMinutes()) {
            if (this.getSeconds() > highscore.getSeconds())
                return 1;
            else if (this.getSeconds() == highscore.getSeconds())
                return 0;
            else
                return -1;
        }
        else
            return 0;
    }

    //  Generates "time strings"
    public String getCorrectedTimeString() {
        String time;
        if (minutes < 10) {
            if (seconds < 10)
                time = ("0" + minutes + ":0" + seconds);
            else
                time = ("0" + minutes + ":" + seconds);
        }
        else {
            if (seconds < 10)
                time = (minutes + ":0" + seconds);
            else
                time = (minutes + ":" + seconds);
        }
        return time;
    }
}
