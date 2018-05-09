package com.bgauthey.speedotracker.speedtracking.instantspeed;

import com.bgauthey.speedotracker.service.LocationService;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class InstantSpeedPresenter implements InstantSpeedContract.Presenter {

    private InstantSpeedContract.View mView;
    private LocationService mLocationService;

    public InstantSpeedPresenter(InstantSpeedContract.View view, LocationService locationService) {
        mView = view;
        mView.setPresenter(this);

        mLocationService = locationService;
    }

    @Override
    public void start() {
        mLocationService.registerOnLocationServiceSpeedChangedListener(mSpeedChangedListener);
        mView.showSpeed(0);
    }

    @Override
    public void stop() {
        mLocationService.unregisterOnLocationServiceSpeedChangedListener(mSpeedChangedListener);
    }

    private final LocationService.OnLocationServiceSpeedChangedListener mSpeedChangedListener = new LocationService.OnLocationServiceSpeedChangedListener() {
        @Override
        public void onLocationServiceSpeedChangedListener(float speed) {
            mView.showSpeed((int) speed);
        }

        @Override
        public void onLocationServiceSpeedActivityChangedListener(boolean active) {
            // Nothing to do
        }
    };
}
