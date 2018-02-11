package com.example.vereskun.maptest.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vereskun on 11.02.2018.
 */

public class SavedRoute extends RealmObject{

    @PrimaryKey
    private long id;
    private String from;
    private String to;
    private RealmList<RealmLatLng> points;
    private RealmList<RealmLatLng> markers;
    private RealmList<RealmInt> durations;

    public SavedRoute(){
    }

    public SavedRoute(long id, String from, String to, RealmList<RealmLatLng> points, RealmList<RealmInt> durations, RealmList<RealmLatLng> markers) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.points = points;
        this.markers = markers;
        this.durations = durations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setDurations(RealmList<RealmInt> durations) {
        this.durations = durations;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public RealmList<RealmLatLng> getPoints() {
        return points;
    }

    public void setPoints(RealmList<RealmLatLng> points) {
        this.points = points;
    }

    public RealmList<RealmInt> getDurations() {
        return durations;
    }

    public RealmList<RealmLatLng> getMarkers() {
        return markers;
    }

    public void setMarkers(RealmList<RealmLatLng> markers) {
        this.markers = markers;
    }
}
