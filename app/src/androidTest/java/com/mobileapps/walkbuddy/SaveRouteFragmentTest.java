package com.mobileapps.walkbuddy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests SaveRouteFragment with Firebase.
 *
 * NOTE: User must be signed in to app before running this test.
 */

public class SaveRouteFragmentTest {
    private static final String TEST_DEST = "TestDest";
    private static final String TEST_START = "TestStart";
    private static final ArrayList<Double> USER_LAT = new ArrayList<>();
    private static final ArrayList<Double> USER_LNG = new ArrayList<>();
    private static final ArrayList<Double> POI_LAT = new ArrayList<>();
    private static final ArrayList<Double> POI_LNG = new ArrayList<>();
    private static final double DEST_LAT = 0.0, DEST_LNG = 0.0;
    private static final long TIME_IN_MILLIS = 300000;

    @ClassRule
    public static FragmentTestRule<SaveRouteFragment> mFragmentTestRule;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        USER_LAT.add(0.0);
        USER_LNG.add(0.0);
        POI_LAT.add(0.0);
        POI_LNG.add(0.0);
    }

    @Before
    public void setUp() throws Exception {
        SaveRouteFragment fragment = SaveRouteFragment.newInstance(USER_LAT, USER_LNG, DEST_LAT,
                DEST_LNG, POI_LAT, POI_LNG, TIME_IN_MILLIS);
        mFragmentTestRule = new FragmentTestRule<>(fragment);
    }

    @Test
    public void testSaveRouteFragmentInstantiation() throws Exception {
        mFragmentTestRule.launchActivity(null);

        onView(withId(R.id.final_time)).check(matches(isDisplayed()));
        onView(withId(R.id.start_location)).check(matches(isDisplayed()));
        onView(withId(R.id.destination_name)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_cancel_route)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_save_route)).check(matches(isDisplayed()));
    }

    @Test
    public void testSaveRouteButtonSavesRoute() throws Exception {
        mFragmentTestRule.launchActivity(null);

        FirebaseAuth mAuth = mFragmentTestRule.getActivity().mAuth;
        DatabaseReference mDatabase = mFragmentTestRule.getActivity().mDatabase;

        // Populate start and destination and click Save Route
        onView(withId(R.id.start_location)).perform(replaceText(TEST_START));
        onView(withId(R.id.destination_name)).perform(replaceText(TEST_DEST));
        onView(withId(R.id.btn_save_route)).perform(click());

        // Check firebase for route that was just saved
        if(mAuth.getCurrentUser() != null) {
            mDatabase.child("users").child(mAuth.getUid()).child("destinations").child(TEST_DEST).child("routes").child("0").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        Route route = dataSnapshot.getValue(Route.class);
                        Assert.assertEquals(TEST_DEST, route.getDestinationName());
                        Assert.assertEquals(TEST_START, route.getStartLocationName());
                        Assert.assertEquals(USER_LAT, route.getVerticesLat());
                        Assert.assertEquals(USER_LNG, route.getVerticesLng());
                        Assert.assertEquals(POI_LAT, route.getPoiLat());
                        Assert.assertEquals(POI_LNG, route.getPoiLng());
                        Assert.assertEquals(TIME_IN_MILLIS, route.getTimeInMillis());
                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            throw new Exception();
        }
    }

    @Test
    public void testCancelSaveRoute() throws Exception {
        mFragmentTestRule.launchActivity(null);

        onView(withId(R.id.btn_cancel_route)).perform(click());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        mFragmentTestRule.launchActivity(null);

        mFragmentTestRule.getActivity().deleteDestination(TEST_DEST);
    }
}
