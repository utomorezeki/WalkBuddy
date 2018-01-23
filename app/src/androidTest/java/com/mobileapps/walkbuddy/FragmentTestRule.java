package com.mobileapps.walkbuddy;

import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;

/**
 * Created by kurti on 11/20/2017.
 */

public class FragmentTestRule<F extends Fragment> extends ActivityTestRule<TestActivity> {


    private F mFragment;

    public FragmentTestRule(final F fragment) {
        super(TestActivity.class, true, false);
        mFragment = fragment;
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, mFragment);
                transaction.commit();
            }
        });
    }

    public F getmFragment() {
        return mFragment;
    }
}
