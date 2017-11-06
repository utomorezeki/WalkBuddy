package com.mobileapps.walkbuddy;

import com.mobileapps.walkbuddy.models.Route;

import java.util.Comparator;

/**
 * Created by kurti on 11/6/2017.
 */

public class RouteTimeComparator implements Comparator<Route> {
    @Override
    public int compare(Route route, Route t1) {
        return (int) (route.getTimeInMillis() - t1.getTimeInMillis());
    }
}
