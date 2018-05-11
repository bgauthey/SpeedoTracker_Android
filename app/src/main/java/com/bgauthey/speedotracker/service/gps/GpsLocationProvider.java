package com.bgauthey.speedotracker.service.gps;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.bgauthey.speedotracker.service.LocationService;

import java.util.concurrent.TimeUnit;

/**
 * Provides a {@link LocationService} that uses GPS to get location and speed.
 */
public final class GpsLocationProvider extends LocationService implements LocationListener {

    private static final String TAG = GpsLocationProvider.class.getSimpleName();

    private static final float FACTOR_M_PER_S_TO_KM_PER_H = 3.6f;

    /**
     * Minimum speed to consider user is moving (in m/s)
     */
    @VisibleForTesting
    static final float MIN_SPEED_RUNNING = 1f / FACTOR_M_PER_S_TO_KM_PER_H; // 3 km/h (in m/s)

    private GpsLocationCallback mLocationCallback;
    private Location mStartLocation;
    private Location mLastLocation;
    private boolean mTrackingRunning;
    private boolean mSpeedActive = false;
    /**
     * Distance between location where speed was activated and location where speed was deactivated (in meters)
     */
    private float mDistance;
    /**
     * Elapsed time between speed activation and speed deactivation (in seconds)
     */
    private long mTimeElapsed;

    @SuppressLint("StaticFieldLeak")
    private static GpsLocationProvider sInstance = null;

    GpsLocationProvider(GpsLocationCallback callback) {
        initParameters();
        mLocationCallback = callback;
//        initComponents(context);
    }

    /**
     * Get instance of {@link GpsLocationProvider}.
//     * @param context the application context
     * @return instance
     */
    public static GpsLocationProvider getInstance(GpsLocationCallback locationCallback) {
        if (sInstance == null) {
            sInstance = new GpsLocationProvider(locationCallback);
        }
        return sInstance;
    }

//    private void initComponents(Context context) {
//        Log.d(TAG, "initComponents");
//    }

    private void initParameters() {
        mTrackingRunning = false;
        mDistance = 0f;
        mTimeElapsed = 0L;
        mStartLocation = new Location(DefaultGpsLocationCallback.LOCATION_PROVIDER);
        mLastLocation = new Location(DefaultGpsLocationCallback.LOCATION_PROVIDER);
    }

    private void updateTrackingState(boolean running) {
        mTrackingRunning = running;
        notifyOnStateChanged(running);
    }

    private void updateLocationSpeed(float speed, Location location) {
        notifyOnSpeedChanged(convertMsToKmH(speed), location);
    }

    private void updateSpeedActive(boolean active) {
        mSpeedActive = active;
        notifyOnSpeedActivityChanged(active);
    }

    private void resetLocations() {
        mStartLocation.reset();
        mLastLocation.reset();
    }

    private void computeSectionParamsToLocation(Location toLocation) {
        mDistance = computeDistance(mStartLocation, toLocation);
        mTimeElapsed = computeTimeElapsed(mStartLocation, toLocation);
    }

    private float computeDistance(Location from, Location to) {
        return Math.max(0, from.distanceTo(to));
    }

    private long computeTimeElapsed(Location from, Location to) {
        return Math.max(0, TimeUnit.NANOSECONDS.toSeconds(from.getElapsedRealtimeNanos() - to.getElapsedRealtimeNanos()));
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

    ///////////////////////////////////////////////////////////////////////////
    // Interface implementation
    ///////////////////////////////////////////////////////////////////////////

    //region LocationService
    @Override
    public boolean isTrackingReady() {
        return mLocationCallback.isTrackingReady();
    }

    @Override
    public boolean isTrackingRunning() {
        return mTrackingRunning;
    }

    @Override
    public void startTracking() {
        if (!isTrackingReady()) {
            return;
        }
        mLocationCallback.startTracking(this);
//        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_INTERVAL_TIME, MIN_INTERVAL_DISTANCE, this);
        updateTrackingState(true);
    }

    @Override
    public void stopTracking() {
        mLocationCallback.stopTracking(this);
//        mLocationManager.removeUpdates(this);
        updateTrackingState(false);
    }

    @Override
    public boolean isSpeedActive() {
        return mSpeedActive;
    }

    @Override
    public float getAverageSpeedHistory() {
        return convertMsToKmH(mDistance / mTimeElapsed);
    }
    //endregion

    //region LocationListener
    @Override
    public void onLocationChanged(Location location) {
//        Log.d(TAG, "onLocationChanged: " + location);
        mLastLocation.set(location);
        if (location.getSpeed() >= MIN_SPEED_RUNNING && mStartLocation.getSpeed() == 0) {
            // Starting point for tracking
            mStartLocation.set(location);
            updateSpeedActive(true);
        } else if (location.getSpeed() < MIN_SPEED_RUNNING && mStartLocation.getSpeed() > 0) {
            // Ending point for tracking
            // compute distance and elapsed time
            computeSectionParamsToLocation(location);
            // Reset stored location to be ready for next record
            resetLocations();
            // notify about speed activity change
            updateSpeedActive(false);
        }
        updateLocationSpeed(location.getSpeed(), location);
    }

    @Override
    public void onProviderDisabled(String provider) {
//        Log.d(TAG, "onProviderDisabled: " + provider);
        // When provider is disabled, record stopped
        // Compute section params according to the last known location
        computeSectionParamsToLocation(mLastLocation);
        // Reset locations
        resetLocations();
        // Update and notify about state's changes
        updateTrackingState(false);
        updateSpeedActive(false);
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Log.d(TAG, "onProviderEnabled: " + provider);
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
    //endregion
}
