package com.mobileapps.walkbuddy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mobileapps.walkbuddy.walkbuddy.R;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests AccountFragment with Firebase changes.
 *
 * NOTE: User must be signed in to app before running this test.
 */

public class AccountFragmentTest {
    private static final String TEST_NAME = "TestName";
    private static String priorName;

    @ClassRule
    public static FragmentTestRule<AccountFragment> mFragmentTestRule;

    @Before
    public void setUp() throws Exception {
        try {
            AccountFragment fragment = AccountFragment.class.newInstance();
            mFragmentTestRule = new FragmentTestRule<>(fragment);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAccountFragmentInstantiation() throws Exception {
        mFragmentTestRule.launchActivity(null);

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_email)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_password)).check(matches(isDisplayed()));
        onView(withId(R.id.confirm_password)).check(matches(isDisplayed()));
        onView(withId(R.id.save_changes)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out)).check(matches(isDisplayed()));
    }

    @Test
    public void testAccountFragmentUpdateAccount() throws Exception {
        mFragmentTestRule.launchActivity(null);

        final FirebaseAuth mAuth = mFragmentTestRule.getActivity().mAuth;
        DatabaseReference mDatabase = mFragmentTestRule.getActivity().mDatabase;

        if(mAuth.getCurrentUser() != null) {
            mDatabase.child("users").child(mAuth.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    priorName = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            throw new Exception("Current user could not be validated");
        }

        // Populate start and destination and click Save Route
        onView(withId(R.id.edit_name)).perform(replaceText(TEST_NAME));
        onView(withId(R.id.save_changes)).perform(click());

        // Check firebase for updated user profile
        if(mAuth.getCurrentUser() != null) {
            mDatabase.child("users").child(mAuth.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Assert.assertEquals(TEST_NAME, dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            throw new Exception("Current user could not be validated");
        }
    }

    @AfterClass
    public static void tearDown() {
        mFragmentTestRule.launchActivity(null);

        final FirebaseAuth mAuth = mFragmentTestRule.getActivity().mAuth;
        DatabaseReference mDatabase = mFragmentTestRule.getActivity().mDatabase;

        if(mAuth.getCurrentUser() != null) {
            mDatabase.child("users").child(mAuth.getUid()).child("name").setValue(priorName);
        }
    }
}
