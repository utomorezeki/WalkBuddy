package com.mobileapps.walkbuddy;

import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.models.Route;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kurti on 11/19/2017.
 */

public class DestinationUnitTest {
    @Test
    public void testGetRoutes() {
        List<Route> routes = new ArrayList<>();
        Route route = routeGenHelper();
        routes.add(route);

        String destinationName = "destination";

        Destination destination = new Destination(routes, destinationName);

        Assert.assertEquals(routes, destination.getRoutes());
        Assert.assertEquals(1, destination.getRoutes().size());
    }

    @Test
    public void testGetRoutesEmptyRoutes() {
        List<Route> routes = new ArrayList<>();
        String destinationName = "destination";
        Destination destination = new Destination(routes, destinationName);

        Assert.assertEquals(routes, destination.getRoutes());
        Assert.assertEquals(0, destination.getRoutes().size());
    }

    @Test
    public void testGetDestinationName() {
        List<Route> routes = new ArrayList<>();
        String destinationName = "destination";
        Destination destination = new Destination(routes, destinationName);

        Assert.assertEquals(destinationName, destination.getDestinationName());
    }

    /**
     * Helper method to generate basic Route used in test cases.
     */
    private Route routeGenHelper() {
        List<Double> list = new ArrayList<>();
        list.add(0.0);

        return new Route("destination", "start", 0, list, list, list, list);
    }
}
