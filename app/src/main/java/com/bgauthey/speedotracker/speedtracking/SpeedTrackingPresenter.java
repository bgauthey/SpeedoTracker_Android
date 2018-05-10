package com.bgauthey.speedotracker.speedtracking;

import android.location.Location;

import com.bgauthey.speedotracker.service.LocationService;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class SpeedTrackingPresenter implements SpeedTrackingContract.Presenter {

    private SpeedTrackingContract.View mView;
    private LocationService mLocationService;

    private boolean mInstantSpeedScreenDisplayed = false;

    public SpeedTrackingPresenter(SpeedTrackingContract.View view, LocationService locationService) {
        mView = view;
        mLocationService = locationService;
    }

    @Override
    public void start() {
        mLocationService.registerOnLocationServiceStateChangedListener(mLocationServiceStateChangedListener);
        mLocationService.registerOnLocationServiceSpeedChangedListener(mOnLocationServiceSpeedChangedListener);
    }

    @Override
    public void stop() {
        mLocationService.unregisterOnLocationServiceStateChangedListener(mLocationServiceStateChangedListener);
        mLocationService.unregisterOnLocationServiceSpeedChangedListener(mOnLocationServiceSpeedChangedListener);
    }

    @Override
    public void toggleTracking() {
        if (!mLocationService.isTrackingReady()) {
            mView.showTrackingNotReady();
        } else if (!mLocationService.isTrackingRunning()) {
            mLocationService.startTracking();
        } else {
            mLocationService.stopTracking();
        }
    }

    @Override
    public void startTracking() {
        mLocationService.startTracking();
    }

    private void showInstantSpeedScreen(boolean show) {
        if (mInstantSpeedScreenDisplayed == show) {
            return;
        }
        mInstantSpeedScreenDisplayed = show;
        if (show) {
            mView.showInstantSpeedScreen();
        } else {
            mView.showFeedbackScreen();
        }
    }

    private final LocationService.OnLocationServiceStateChangedListener mLocationServiceStateChangedListener
            = new LocationService.OnLocationServiceStateChangedListener() {
        @Override
        public void onLocationServiceStateChangedListener(boolean enabled) {
            showInstantSpeedScreen(true);
            mView.updateButtonState(!enabled);
        }
    };

    private final LocationService.OnLocationServiceSpeedChangedListener mOnLocationServiceSpeedChangedListener
            = new LocationService.OnLocationServiceSpeedChangedListener() {

        @Override
        public void onLocationServiceSpeedActivityChangedListener(boolean active) {
            showInstantSpeedScreen(active);
        }

        @Override
        public void onLocationServiceSpeedChangedListener(float speed, Location location) {
            // Nothing to do
//            if (speed == 0) {
//                showInstantSpeedScreen(false);
//            } else {
//                showInstantSpeedScreen(true);
//            }
        }
    };
}
