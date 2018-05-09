package com.bgauthey.speedotracker.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.bgauthey.speedotracker.util.PermissionUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author bgauthey created on 08/05/2018.
 */

public final class GpsLocationService extends LocationService {

    private static final String TAG = GpsLocationService.class.getSimpleName();

    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private static final long INTERVAL_TIME = 1_000L * 2; // 2 s
    private static final long INTERVAL_DISTANCE = 100; // 100 m

    private LocationManager mLocationManager = null;
    private LocationListenerImpl mLocationListener = null;
    private Context mContext;
    private boolean mTrackingEnabled = false;

    @SuppressLint("StaticFieldLeak")
    private static GpsLocationService sInstance = null;

    private GpsLocationService(Context context) {
        mContext = context;
        initComponents(context);
    }

    public static GpsLocationService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GpsLocationService(context);
        }
        return sInstance;
    }

    @Override
    public boolean isTrackingReady() {
        return PermissionUtils.isLocationPermissionGranted(mContext) && mLocationManager.isProviderEnabled(LOCATION_PROVIDER);
    }

    @Override
    public boolean isTrackingStarted() {
        return mTrackingEnabled;
    }

    @Override
    public void toggleTracking() {
        if (mTrackingEnabled) {
            stopTracking();
        } else {
            startTracking();
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    public void startTracking() {
        if (!isTrackingReady()) {
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, INTERVAL_TIME, INTERVAL_DISTANCE, mLocationListener);
        updateTrackingState(true);
    }

    @Override
    public void stopTracking() {
        mLocationManager.removeUpdates(mLocationListener);
        updateTrackingState(false);
    }

    @Override
    public float getAverageSpeedHistory() {
        return convertMsToKmH(mLocationListener.getDistance() / mLocationListener.getTimeElapsed());
    }

    private void initComponents(Context context) {
        Log.d(TAG, "initComponents");
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListenerImpl(LOCATION_PROVIDER);
    }

    private class LocationListenerImpl implements android.location.LocationListener {
        Location beginLocation;
        float distance = 0f; // in meters
        long timeElapsed = 0L; // in seconds

        LocationListenerImpl(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            beginLocation = new Location(provider);
        }

        /**
         * Get distance between location where speed was activated and location where speed was deactivated.
         * @return distance in meters
         */
        public float getDistance() {
            return distance;
        }

        /**
         * Get elapsed time between speed activation and speed deactivation
         * @return elapsed time in seconds
         */
        public long getTimeElapsed() {
            return timeElapsed;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            if (location.getSpeed() > 0 && beginLocation.getSpeed() == 0) {
                beginLocation.set(location);
                notifyOnSpeedActivityChanged(true);
            } else if (location.getSpeed() == 0 && beginLocation.getSpeed() > 0) {
                // compute distance and elapsed time
                distance = computeDistance(beginLocation, location);
                timeElapsed = computeTimeElapsed(beginLocation, location);
                // Reset begin location to be ready for next record
                resetBeginLocation();
                // notify about speed activity change
                notifyOnSpeedActivityChanged(false);
            }
            updateLocationSpeed(location.getSpeed());
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
            updateTrackingState(false);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
            updateTrackingState(true);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: " + provider + ", status = " + status);
            switch (status) {
                case LocationProvider.AVAILABLE:
                    updateTrackingState(true);
                    break;
                default:
                    updateTrackingState(false);
            }
        }

        private void resetBeginLocation() {
            beginLocation.reset();
        }

        private float computeDistance(Location from, Location to) {
            return from.distanceTo(to);
        }

        private long computeTimeElapsed(Location from, Location to) {
            return TimeUnit.NANOSECONDS.toSeconds(from.getElapsedRealtimeNanos() - to.getElapsedRealtimeNanos());
        }

        private boolean isTargetProvider(String provider) {
            return LOCATION_PROVIDER.equals(provider);
        }
    }

    private void updateTrackingState(boolean enabled) {
        mTrackingEnabled = enabled;
        notifyOnStateChanged(enabled);
    }

    private void updateLocationSpeed(float speed) {
        notifyOnSpeedChanged(convertMsToKmH(speed));
    }

    /**
     * Convert m/s speed to km/h
     *
     * @param value value to convert
     * @return value expressed in km/h
     */
    private static int convertMsToKmH(float value) {
        return Math.round(value * 3.6f);
    }
}
