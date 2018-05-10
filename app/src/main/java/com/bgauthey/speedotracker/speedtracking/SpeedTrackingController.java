package com.bgauthey.speedotracker.speedtracking;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.bgauthey.speedotracker.Injection;
import com.bgauthey.speedotracker.R;
import com.bgauthey.speedotracker.speedtracking.feedback.FeedbackFragment;
import com.bgauthey.speedotracker.speedtracking.feedback.FeedbackPresenter;
import com.bgauthey.speedotracker.speedtracking.instantspeed.InstantSpeedFragment;
import com.bgauthey.speedotracker.speedtracking.instantspeed.InstantSpeedPresenter;
import com.bgauthey.speedotracker.util.ActivityUtils;

/**
 * Class that creates screens (MVP views using fragment).
 */
public class SpeedTrackingController {

    private final FragmentActivity mFragmentActivity;

    public SpeedTrackingController(FragmentActivity fragmentActivity) {
        mFragmentActivity = fragmentActivity;
    }

    public static SpeedTrackingController createSpeedTrackingScreens(@NonNull FragmentActivity activity) {
        SpeedTrackingController controller = new SpeedTrackingController(activity);
        controller.initSpeedTrackingScreens();
        return controller;
    }

    private void initSpeedTrackingScreens() {
        // Instant speed screen
        InstantSpeedFragment instantSpeedFragment = findOrCreateInstantSpeedFragment();
        InstantSpeedPresenter instantSpeedPresenter = createInstantSpeedPresenter(instantSpeedFragment);
        instantSpeedFragment.setPresenter(instantSpeedPresenter);

        // Feedback screen
        FeedbackFragment feedbackFragment = findOrCreateFeedbackFragment();
        FeedbackPresenter feedbackPresenter = createFeedbackPresenter(feedbackFragment);
        feedbackFragment.setPresenter(feedbackPresenter);
    }

    private InstantSpeedFragment findOrCreateInstantSpeedFragment() {
        InstantSpeedFragment instantSpeedFragment =
                (InstantSpeedFragment) getFragmentById(R.id.fl_fragment_container);

        if (instantSpeedFragment == null) {
            instantSpeedFragment = InstantSpeedFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), instantSpeedFragment, R.id.fl_fragment_container);
        }

        return instantSpeedFragment;
    }

    private FeedbackFragment findOrCreateFeedbackFragment() {
        FeedbackFragment feedbackFragment = (FeedbackFragment) getFragmentById(R.id.fl_bottom_sheet_container);

        if (feedbackFragment == null) {
            feedbackFragment = FeedbackFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), feedbackFragment, R.id.fl_bottom_sheet_container);
        }

        return feedbackFragment;
    }

    private InstantSpeedPresenter createInstantSpeedPresenter(InstantSpeedFragment fragment) {
        return new InstantSpeedPresenter(fragment,
                Injection.provideLocationProvider(mFragmentActivity.getApplicationContext()));
    }

    private FeedbackPresenter createFeedbackPresenter(FeedbackFragment fragment) {
        return new FeedbackPresenter(fragment,
                Injection.provideLocationProvider(mFragmentActivity.getApplicationContext()));
    }

    private Fragment getFragmentById(int container) {
        return getSupportFragmentManager().findFragmentById(container);
    }

    private FragmentManager getSupportFragmentManager() {
        return mFragmentActivity.getSupportFragmentManager();
    }
}
