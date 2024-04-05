package com.example.gmap;

public class LocationData {
    private double latitude;
    private double longitude;

    public LocationData() {
    }

    public LocationData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
