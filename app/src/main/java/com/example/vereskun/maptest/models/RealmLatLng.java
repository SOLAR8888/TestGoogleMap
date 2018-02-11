package com.example.vereskun.maptest.models;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmObject;

/**
 * Created by vereskun on 11.02.2018.
 */

public class RealmLatLng extends RealmObject{
    private double latitude;
    private double longitude;

    public RealmLatLng() {
    }

    @Override
    public String toString() {
        return "RealmLatLng{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public RealmLatLng(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
