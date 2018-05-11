package com.bgauthey.speedotracker.speedtracking.instantspeed;

import android.location.Location;

import com.bgauthey.speedotracker.Constants;
import com.bgauthey.speedotracker.service.LocationService;

import java.text.DecimalFormat;

/**
 * Listens to {@link LocationService} updates and updates the UI as required.
 */
public class InstantSpeedPresenter implements InstantSpeedContract.Presenter {

    private InstantSpeedContract.View mView;
    private LocationService mLocationService;

    public InstantSpeedPresenter(InstantSpeedContract.View view, LocationService locationService) {
        mView = view;
        mLocationService = locationService;
    }

    @Override
    public void start() {
        mLocationService.registerOnLocationServiceSpeedChangedListener(mSpeedChangedListener);
    }

    @Override
    public void stop() {
        mLocationService.unregisterOnLocationServiceSpeedChangedListener(mSpeedChangedListener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Final implementations
    ///////////////////////////////////////////////////////////////////////////

    private final LocationService.OnLocationServiceSpeedChangedListener mSpeedChangedListener = new LocationService.OnLocationServiceSpeedChangedListener() {
        @Override
        public void onLocationServiceSpeedChangedListener(float speed, Location location) {
            mView.showSpeed(new DecimalFormat("#").format(speed));
            if (Constants.SHOW_DEBUG_INFO) {
                mView.showLocationDebug(location);
            }
        }

        @Override
        public void onLocationServiceSpeedActivityChangedListener(boolean active) {
            // Nothing to do
        }
    };
}
