package com.bgauthey.speedotracker.speedtracking;

import android.location.Location;

import com.bgauthey.speedotracker.service.LocationProvider;

/**
 * Listens to user actions from the UI {@link SpeedTrackingActivity}, retrieves the data and updates
 * the UI as required.
 */
public class SpeedTrackingPresenter implements SpeedTrackingContract.Presenter {

    private SpeedTrackingContract.View mView;
    private LocationProvider mLocationProvider;

    public SpeedTrackingPresenter(SpeedTrackingContract.View view, LocationProvider locationProvider) {
        mView = view;
        mLocationProvider = locationProvider;
    }

    @Override
    public void start() {
        registerListeners();
        showInstantSpeedScreen(mLocationProvider.isSpeedActive());
    }

    @Override
    public void stop() {
        unregisterListeners();
    }

    @Override
    public void toggleTracking() {
        if (!mLocationProvider.isTrackingReady()) {
            mView.showTrackingNotReady();
        } else if (!mLocationProvider.isTrackingRunning()) {
            mLocationProvider.startTracking();
        } else {
            mLocationProvider.stopTracking();
        }
    }

    @Override
    public void startTracking() {
        mLocationProvider.startTracking();
    }

    void registerListeners() {
        mLocationProvider.registerOnLocationServiceStateChangedListener(mLocationServiceStateChangedListener);
        mLocationProvider.registerOnLocationServiceSpeedChangedListener(mOnSpeedChangedListener);
    }

    void unregisterListeners() {
        mLocationProvider.unregisterOnLocationServiceStateChangedListener(mLocationServiceStateChangedListener);
        mLocationProvider.unregisterOnLocationServiceSpeedChangedListener(mOnSpeedChangedListener);
    }

    private void showInstantSpeedScreen(boolean show) {
//        if (mInstantSpeedScreenDisplayed == show) {
//            return;
//        }
//        mInstantSpeedScreenDisplayed = show;
        if (show) {
            mView.showInstantSpeedScreen();
        } else {
            mView.showFeedbackScreen();
        }
    }

    private final LocationProvider.OnSpeedRecordingStateChangedListener mLocationServiceStateChangedListener
            = new LocationProvider.OnSpeedRecordingStateChangedListener() {
        @Override
        public void onSpeedRecordingStateChanged(boolean enabled) {
            showInstantSpeedScreen(enabled);
            mView.updateButtonState(!enabled);
        }
    };

    private final LocationProvider.OnSpeedChangedListener mOnSpeedChangedListener
            = new LocationProvider.OnSpeedChangedListener() {

        @Override
        public void onSpeedActivityChanged(boolean active) {
            showInstantSpeedScreen(active);
        }

        @Override
        public void onSpeedChanged(float speed, Location location) {
            // Nothing to do
        }
    };
}
