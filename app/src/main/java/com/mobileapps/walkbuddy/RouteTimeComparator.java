package com.mobileapps.walkbuddy;

import com.mobileapps.walkbuddy.models.Route;

import java.util.Comparator;

/**
 * Compares routes based on their route time.
 */
public class RouteTimeComparator implements Comparator<Route> {
    @Override
    public int compare(Route route, Route t1) {
        return (int) (route.getTimeInMillis() - t1.getTimeInMillis());
    }
}
