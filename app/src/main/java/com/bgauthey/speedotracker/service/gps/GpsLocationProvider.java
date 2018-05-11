package com.bgauthey.speedotracker.service.gps;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.bgauthey.speedotracker.Constants;
import com.bgauthey.speedotracker.service.LocationService;

import java.util.concurrent.TimeUnit;

/**
 * Provides a {@link LocationService} that uses GPS to get location and speed.
 */
public class GpsLocationProvider extends LocationService implements LocationListener {

    private static final String TAG = GpsLocationProvider.class.getSimpleName();

    private static final float FACTOR_M_PER_S_TO_KM_PER_H = 3.6f;

    /**
     * Minimum speed to consider user is moving (in m/s)
     */
    @VisibleForTesting
    static final float MIN_SPEED_RUNNING = Constants.MINIMUM_SPEED_RUNNING_KM_PER_H / FACTOR_M_PER_S_TO_KM_PER_H; // convert km/h to m/s)

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
    private int mTimeElapsed;
    /**
     * Average speed computed between speed activation and speed deactivation (in m/s)
     */
    private float mAverageSpeed;

    private static GpsLocationProvider sInstance = null;

    GpsLocationProvider(GpsLocationCallback callback) {
        initParameters();
        mLocationCallback = callback;
    }

    /**
     * Get instance of {@link GpsLocationProvider}.
     *
     * @param locationCallback a {@link GpsLocationCallback} responsive of doing action on system when requested
     * @return instance
     */
    public static GpsLocationProvider getInstance(GpsLocationCallback locationCallback) {
        if (sInstance == null) {
            sInstance = new GpsLocationProvider(locationCallback);
        }
        return sInstance;
    }

    private void initParameters() {
        mTrackingRunning = false;
        mDistance = 0f;
        mTimeElapsed = 0;
        mAverageSpeed = 0f;
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
        mAverageSpeed = mDistance / mTimeElapsed;
        notifyOnAverageSpeedChanged(convertMsToKmH(mAverageSpeed), mDistance, mTimeElapsed);
    }

    private float computeDistance(Location from, Location to) {
        return Math.max(0, from.distanceTo(to));
    }

    private int computeTimeElapsed(Location from, Location to) {
        return (int) Math.max(0, TimeUnit.NANOSECONDS.toSeconds(to.getElapsedRealtimeNanos() - from.getElapsedRealtimeNanos()));
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
        updateTrackingState(true);
    }

    @Override
    public void stopTracking() {
        mLocationCallback.stopTracking(this);
        updateTrackingState(false);
    }

    @Override
    public boolean isSpeedActive() {
        return mSpeedActive;
    }

    @Override
    public float getAverageSpeedHistory() {
        return convertMsToKmH(mAverageSpeed);
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
