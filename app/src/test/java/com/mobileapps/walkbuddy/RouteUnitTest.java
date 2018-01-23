package com.mobileapps.walkbuddy;

import com.mobileapps.walkbuddy.models.Route;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kurti on 11/19/2017.
 */

public class RouteUnitTest {
    private static final String destinationName = "destination";
    private static final String startLocationName = "start";
    private static final Double coord = 0.0;
    private static final long timeInMillis = 0;

    @Test
    public void testGetDestinationName() {
        Route route = routeGenHelper();

        Assert.assertEquals(destinationName, route.getDestinationName());
    }

    @Test
    public void testGetStartLocationName() {
        Route route = routeGenHelper();

        Assert.assertEquals(startLocationName, route.getStartLocationName());
    }

    @Test
    public void testGetTimeInMillis() {
        Route route = routeGenHelper();

        Assert.assertEquals(timeInMillis, route.getTimeInMillis());
    }

    @Test
    public void testGetVerticesLat() {
        Route route = routeGenHelper();
        List<Double> verticesLat = new ArrayList<>();
        verticesLat.add(coord);

        Assert.assertEquals(verticesLat, route.getVerticesLat());
    }

    @Test
    public void testGetVerticesLng() {
        Route route = routeGenHelper();
        List<Double> verticesLng = new ArrayList<>();
        verticesLng.add(coord);

        Assert.assertEquals(verticesLng, route.getVerticesLng());
    }

    @Test
    public void testGetPoiLat() {
        Route route = routeGenHelper();
        List<Double> poiLat = new ArrayList<>();
        poiLat.add(coord);

        Assert.assertEquals(poiLat, route.getPoiLat());
    }

    @Test
    public void testGetPoiLng() {
        Route route = routeGenHelper();
        List<Double> poiLng = new ArrayList<>();
        poiLng.add(coord);

        Assert.assertEquals(poiLng, route.getPoiLng());
    }

    /**
     * Helper method to generate basic Route used in test cases.
     */
    private Route routeGenHelper() {
        List<Double> list = new ArrayList<>();
        list.add(coord);

        return new Route(destinationName, startLocationName, timeInMillis, list, list, list, list);
    }
}
