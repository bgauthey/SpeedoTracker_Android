package com.bgauthey.speedotracker.service;

/**
 * A {@link LocationService} for unit tests. Easy way to trigger events.
 */
public class LocationProviderForTest extends LocationService {

    private boolean mTrackingReady = false;
    private boolean mTrackingRunning = false;
    private boolean mSpeedRunning = false;
    private float mAverageSpeed = 0f;

    @Override
    public boolean isTrackingReady() {
        return mTrackingReady;
    }

    @Override
    public boolean isTrackingRunning() {
        return mTrackingRunning;
    }

    @Override
    public void startTracking() {
        triggerStateChanged(true);
    }

    @Override
    public void stopTracking() {
        triggerStateChanged(false);
    }

    @Override
    public boolean isSpeedActive() {
        return false;
    }

    @Override
    public float getAverageSpeedHistory() {
        return mAverageSpeed;
    }

    public void setTrackingReady(boolean trackingReady) {
        mTrackingReady = trackingReady;
    }

    public void setTrackingRunning(boolean trackingRunning) {
        mTrackingRunning = trackingRunning;
    }

    public void setSpeedRunning(boolean speedRunning) {
        mSpeedRunning = speedRunning;
    }

    public void triggerStateChanged(boolean active) {
        mTrackingRunning = active;
        notifyOnStateChanged(active);
    }

    public void triggerSpeedActivityChanged(boolean active) {
        mSpeedRunning = false;
        notifyOnSpeedActivityChanged(active);
    }

    public void triggerSpeedChanged(float newSpeed) {
        notifyOnSpeedChanged(newSpeed, null);
    }

    public void triggerAverageSpeedChanged(float newAverageSpeed) {
        mAverageSpeed = newAverageSpeed;
        notifyOnAverageSpeedChanged(mAverageSpeed, 0, 0);
    }
}
