package com.bgauthey.speedotracker.speedtracking.instantspeed;

import android.location.Location;

import com.bgauthey.speedotracker.Constants;
import com.bgauthey.speedotracker.service.LocationProvider;

import java.text.DecimalFormat;

/**
 * Listens to {@link LocationProvider} updates and updates the UI as required.
 */
public class InstantSpeedPresenter implements InstantSpeedContract.Presenter {

    private InstantSpeedContract.View mView;
    private LocationProvider mLocationProvider;

    public InstantSpeedPresenter(InstantSpeedContract.View view, LocationProvider locationProvider) {
        mView = view;
        mLocationProvider = locationProvider;
    }

    @Override
    public void start() {
        mLocationProvider.registerOnLocationServiceSpeedChangedListener(mSpeedChangedListener);
    }

    @Override
    public void stop() {
        mLocationProvider.unregisterOnLocationServiceSpeedChangedListener(mSpeedChangedListener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Final implementations
    ///////////////////////////////////////////////////////////////////////////

    private final LocationProvider.OnLocationServiceSpeedChangedListener mSpeedChangedListener = new LocationProvider.OnLocationServiceSpeedChangedListener() {
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
