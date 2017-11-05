package com.mobileapps.walkbuddy.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kurti on 11/2/2017.
 */

public class Route {
    private String routeName;
    private String startLocationName;
    private long timeInMillis;
    private List<Double> verticesLat;
    private List<Double> verticesLng;

    public Route() {}

    public Route(String routeName, String startLocationName, long timeInMillis, List<Double> verticesLat, List<Double> verticesLng) {
        this.routeName = routeName;
        this.startLocationName = startLocationName;
        this.timeInMillis = timeInMillis;
        this.verticesLat = verticesLat;
        this.verticesLng = verticesLng;
    }

    public ArrayList<LatLng> getUserRoute(){
        ArrayList<LatLng> result = new ArrayList<LatLng>();
        for(int i = 0; i < verticesLat.size(); i++){
            result.add(new LatLng(verticesLat.get(i),verticesLng.get(i)));
        }
        return result;
    }
    public String getRouteName() { return this.routeName; }

    public String getStartLocationName() { return this.startLocationName; }

    public long getTimeInMillis() { return this.timeInMillis; }
}
