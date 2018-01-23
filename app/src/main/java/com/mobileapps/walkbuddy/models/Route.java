package com.mobileapps.walkbuddy.models;

import java.io.Serializable;
import java.util.List;

/**
 * Route model for storing route to a destination.
 */

public class Route implements Serializable {
    private String destinationName;
    private String startLocationName;
    private long timeInMillis;
    private List<Double> verticesLat;
    private List<Double> verticesLng;
    private List<Double> poiLat;
    private List<Double> poiLng;

    public Route() {
        // Empty constructor needed for firebase
    }

    public Route(String destinationName,
                 String startLocationName,
                 long timeInMillis,
                 List<Double> verticesLat,
                 List<Double> verticesLng,
                 List<Double> poiLat,
                 List<Double> poiLng) {
        this.destinationName = destinationName;
        this.startLocationName = startLocationName;
        this.timeInMillis = timeInMillis;
        this.verticesLat = verticesLat;
        this.verticesLng = verticesLng;
        this.poiLat = poiLat;
        this.poiLng = poiLng;
    }


    public String getDestinationName() { return this.destinationName; }
    public String getStartLocationName() { return this.startLocationName; }
    public long getTimeInMillis() { return this.timeInMillis; }
    public List<Double> getVerticesLat() { return this.verticesLat; }
    public List<Double> getVerticesLng() { return this.verticesLng; }
    public List<Double> getPoiLat() { return this.poiLat; }
    public List<Double> getPoiLng() { return this.poiLng; }
}
