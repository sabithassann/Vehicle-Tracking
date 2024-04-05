package com.example.gmap;

public class Vehicle{

    private double latitude;
    private double longitude;

    public Vehicle() {
    }

    public Vehicle(double latitude, double longitude) {
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
