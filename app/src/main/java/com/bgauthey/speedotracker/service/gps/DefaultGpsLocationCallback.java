package com.bgauthey.speedotracker.service.gps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

import com.bgauthey.speedotracker.util.PermissionUtils;

/**
 * The default implementation of {@link GpsLocationCallback} that uses the system {@link LocationManager}
 * with the {@link LocationManager#GPS_PROVIDER} provider.
 */
public class DefaultGpsLocationCallback implements GpsLocationCallback {

    /**
     * Location provider use to track speed: GPS
     */
    static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    /**
     * Minimum time interval to update the location (in milliseconds)
     */
    private static final long MIN_INTERVAL_TIME = 500L;
    /**
     * Minimum distance interval to update the location (in meters)
     */
    private static final long MIN_INTERVAL_DISTANCE = 0L;

    private Context mContext;
    private LocationManager mLocationManager;

    public DefaultGpsLocationCallback(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean isTrackingReady() {
        return PermissionUtils.isLocationPermissionGranted(mContext) && mLocationManager.isProviderEnabled(LOCATION_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startTracking(LocationListener listener) {
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_INTERVAL_TIME, MIN_INTERVAL_DISTANCE, listener);
    }

    @Override
    public void stopTracking(LocationListener listener) {
        mLocationManager.removeUpdates(listener);
    }
}
