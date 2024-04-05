package com.example.gmap;

public class Warehouse{

    private double latitude;
    private double longitude;

    public Warehouse() {
    }

    public Warehouse(double latitude, double longitude) {
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
