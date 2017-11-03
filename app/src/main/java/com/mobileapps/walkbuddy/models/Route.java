package com.mobileapps.walkbuddy.models;

/**
 * Created by kurti on 11/2/2017.
 */

public class Route {
    private String routeName;
    private String startLocationName;
    private long timeInMillis;

    public Route() {}

    public Route(String routeName, String startLocationName, long timeInMillis) {
        this.routeName = routeName;
        this.startLocationName = startLocationName;
        this.timeInMillis = timeInMillis;
    }

    public String getRouteName() { return this.routeName; }

    public String getStartLocationName() { return this.startLocationName; }

    public long getTimeInMillis() { return this.timeInMillis; }
}
