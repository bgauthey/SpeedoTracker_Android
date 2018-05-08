package com.bgauthey.speedotracker.speedtracking;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bgauthey.speedotracker.service.LocationService;
import com.bgauthey.speedotracker.speedtracking.feedback.FeedbackFragment;
import com.bgauthey.speedotracker.speedtracking.feedback.FeedbackPresenter;
import com.bgauthey.speedotracker.speedtracking.instantspeed.InstantSpeedFragment;
import com.bgauthey.speedotracker.speedtracking.instantspeed.InstantSpeedPresenter;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class SpeedViewPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGER_COUNT = 2;

    private LocationService mLocationService;

    public SpeedViewPagerAdapter(FragmentManager fm, LocationService locationService) {
        super(fm);
        mLocationService = locationService;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                InstantSpeedFragment instantSpeedFragment = InstantSpeedFragment.newInstance();
                new InstantSpeedPresenter(instantSpeedFragment, mLocationService);
                return instantSpeedFragment;
            case 1:
                FeedbackFragment feedbackFragment = FeedbackFragment.newInstance();
                new FeedbackPresenter(feedbackFragment);
                return feedbackFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
