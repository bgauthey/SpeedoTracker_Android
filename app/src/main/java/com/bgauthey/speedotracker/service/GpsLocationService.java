package com.bgauthey.speedotracker.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.bgauthey.speedotracker.util.PermissionUtils;

import java.util.concurrent.TimeUnit;

/**
 * A {@link LocationService} that uses GPS to get location and speed.
 */
public final class GpsLocationService extends LocationService {

    private static final String TAG = GpsLocationService.class.getSimpleName();

    private static final float FACTOR_M_PER_S_TO_KM_PER_H = 3.6f;

    /**
     * Location provider use to track speed: GPS
     */
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    /**
     * Minimum time interval to update the location (in milliseconds)
     */
    private static final long MIN_INTERVAL_TIME = 500L;
    /**
     * Minimum distance interval to update the location (in meters)
     */
    private static final long MIN_INTERVAL_DISTANCE = 0L;

    /**
     * Minimum speed to consider user is moving (in m/s)
     */
    private static final float MIN_SPEED_RUNNING = 3f / FACTOR_M_PER_S_TO_KM_PER_H; // 3 km/h (in m/s)

    private LocationManager mLocationManager = null;
    private GpsLocationListener mLocationListener = null;
    private Context mContext;
    private boolean mTrackingEnabled = false;

    @SuppressLint("StaticFieldLeak")
    private static GpsLocationService sInstance = null;

    private GpsLocationService(Context context) {
        mContext = context;
        initComponents(context);
    }

    /**
     * Get instance of {@link GpsLocationService}.
     * @param context the application context
     * @return instance
     */
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
    public boolean isTrackingRunning() {
        return mTrackingEnabled;
    }

    @Override
    @SuppressLint("MissingPermission")
    public void startTracking() {
        if (!isTrackingReady()) {
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_INTERVAL_TIME, MIN_INTERVAL_DISTANCE, mLocationListener);
//        updateTrackingState(true);
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
        mLocationListener = new GpsLocationListener();
    }

    private class GpsLocationListener implements LocationListener {
        Location beginLocation;
        Location lastLocation;
        float distance = 0f; // in meters
        long timeElapsed = 0L; // in seconds

        GpsLocationListener() {
            beginLocation = new Location(LOCATION_PROVIDER);
            lastLocation = new Location(LOCATION_PROVIDER);
        }

        /**
         * Get distance between location where speed was activated and location where speed was deactivated.
         *
         * @return distance in meters
         */
        public float getDistance() {
            return distance;
        }

        /**
         * Get elapsed time between speed activation and speed deactivation
         *
         * @return elapsed time in seconds
         */
        public long getTimeElapsed() {
            return timeElapsed;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            lastLocation.set(location);
            if (location.getSpeed() >= MIN_SPEED_RUNNING && beginLocation.getSpeed() == 0) {
                // Starting point for tracking
                beginLocation.set(location);
                notifyOnSpeedActivityChanged(true);
            } else if (location.getSpeed() < MIN_SPEED_RUNNING && beginLocation.getSpeed() > 0) {
                // Ending point for tracking
                // compute distance and elapsed time
                updateSectionParamsToLocation(location);
                // Reset stored location to be ready for next record
                resetLocations();
                // notify about speed activity change
                notifyOnSpeedActivityChanged(false);
            }
            updateLocationSpeed(location.getSpeed(), location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
            // When provider is disabled, record stopped
            // Compute section params according to the last known location
            updateSectionParamsToLocation(lastLocation);
            // Reset locations
            resetLocations();
            // Update and notify about state's changes
            updateTrackingState(false);
            notifyOnSpeedActivityChanged(false);
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

        private void resetLocations() {
            beginLocation.reset();
        }

        private void updateSectionParamsToLocation(Location toLocation) {
            distance = computeDistance(beginLocation, toLocation);
            timeElapsed = computeTimeElapsed(beginLocation, toLocation);
        }

        private float computeDistance(Location from, Location to) {
            return Math.max(0, from.distanceTo(to));
        }

        private long computeTimeElapsed(Location from, Location to) {
            return Math.max(0, TimeUnit.NANOSECONDS.toSeconds(from.getElapsedRealtimeNanos() - to.getElapsedRealtimeNanos()));
        }
    }

    private void updateTrackingState(boolean enabled) {
        mTrackingEnabled = enabled;
        notifyOnStateChanged(enabled);
    }

    private void updateLocationSpeed(float speed, Location location) {
        notifyOnSpeedChanged(convertMsToKmH(speed), location);
    }

    /**
     * Convert m/s speed to km/h
     *
     * @param value value to convert
     * @return value expressed in km/h
     */
    private static int convertMsToKmH(float value) {
        return Math.round(value * FACTOR_M_PER_S_TO_KM_PER_H);
    }
}
