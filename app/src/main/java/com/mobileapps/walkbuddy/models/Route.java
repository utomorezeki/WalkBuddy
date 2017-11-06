package com.mobileapps.walkbuddy.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kurti on 11/2/2017.
 */

public class Route implements Serializable {
    private String destinationName;
    private String startLocationName;
    private long timeInMillis;
    private List<Double> verticesLat;
    private List<Double> verticesLng;

    public Route() {}

    public Route(String routeName, String startLocationName, long timeInMillis, List<Double> verticesLat, List<Double> verticesLng) {
        this.destinationName = routeName;
        this.startLocationName = startLocationName;
        this.timeInMillis = timeInMillis;
        this.verticesLat = verticesLat;
        this.verticesLng = verticesLng;
    }

    public String getDestinationName() { return this.destinationName; }

    public String getStartLocationName() { return this.startLocationName; }

    public long getTimeInMillis() { return this.timeInMillis; }

    public List<Double> getVerticesLat() { return this.verticesLat; }
    public List<Double> getVerticesLng() { return this.verticesLng; }
}
