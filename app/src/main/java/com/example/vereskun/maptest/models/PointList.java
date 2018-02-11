package com.example.vereskun.maptest.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by vereskun on 10.02.2018.
 */

public class PointList {

    private LatLng from;
    private LatLng to;
    private ArrayList<LatLng> waipoints;
    private ArrayList<LatLng> allPoints;

    public PointList(){
        this.waipoints = new ArrayList<>();
        this.allPoints = new ArrayList<>();
    }

    public LatLng getFrom() {
        return from;
    }

    public void setFrom(LatLng from) {
        this.from = from;
    }

    public LatLng getTo() {
        return to;
    }

    public void setTo(LatLng to) {
        this.to = to;
    }

    public void addWaypoint(LatLng latLng){
        if (waipoints.size() < 5){
            waipoints.add(latLng);
        }
    }
    public int getPointsCount(){
        int count = 0;
        if (from != null) count+=1;
        if (to != null) count+=1;
        count+=waipoints.size();
        return count;
    }

    public ArrayList<LatLng> getAllPoints(){
        allPoints.clear();
        allPoints.add(from);
        if (waipoints.size() > 0){
            for (int i = 0; i < waipoints.size(); i++) {
                allPoints.add(waipoints.get(i));
            }
        }
        allPoints.add(to);
        return allPoints;
    }

    public ArrayList<LatLng> getWaipoints() {
        return waipoints;
    }
}
