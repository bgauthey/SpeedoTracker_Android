package com.bgauthey.speedotracker.service.gps;

import android.location.LocationListener;

/**
 * Callback used by {@link GpsLocationProvider} to make action relative to the system location
 * manager.
 */
public interface GpsLocationCallback {

    boolean isTrackingReady();

    void startTracking(LocationListener listener);

    void stopTracking(LocationListener listener);
}
