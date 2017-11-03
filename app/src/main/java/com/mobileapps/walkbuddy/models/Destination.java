package com.mobileapps.walkbuddy.models;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by kurti on 11/2/2017.
 */

public class Destination {
    @NotNull
    private List<Route> routes;
    private String destinationName;

    public Destination() {}

    public Destination(List<Route> routes, String destinationName) {
        this.routes = routes;
        this.destinationName = destinationName;
    }

    public List<Route> getRoutes() { return this.routes; }

    public String getDestinationName() { return this.destinationName; }
}
