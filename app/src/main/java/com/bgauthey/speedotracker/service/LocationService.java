package com.bgauthey.speedotracker.service;

import android.location.Location;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A base to create a provider handling location and speed.
 * <p>
 * It provides methods to notify about {@link LocationService} states.
 * <p>
 * {@link OnLocationServiceStateChangedListener} gives feedback about speed recording state.
 * <p>
 * {@link OnLocationServiceSpeedChangedListener} gives feedback about speed changed.
 */
public abstract class LocationService {

    /**
     * Interface definition for a callback to be invoked when speed recording state changed.
     */
    public interface OnLocationServiceStateChangedListener {
        /**
         * Invoked when speed recording state changed.
         *
         * @param enabled true if under recording, false otherwise
         */
        void onLocationServiceStateChangedListener(boolean enabled);
    }

    /**
     * Interface definition for a callback to be invoked when speed changed.
     */
    public interface OnLocationServiceSpeedChangedListener {
        /**
         * Invoked when the location is updated.
         *
         * @param speed    speed computed for this location
         * @param location
         */
        void onLocationServiceSpeedChangedListener(float speed, Location location);

        /**
         * Invoked when speed activity changed. Speed is considered active when greater than 0 and inactive when equal to 0.
         *
         * @param active true if speed is active, false otherwise.
         */
        void onLocationServiceSpeedActivityChangedListener(boolean active);
    }

    private CopyOnWriteArrayList<OnLocationServiceStateChangedListener> mOnLocationServiceStateChangedListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<OnLocationServiceSpeedChangedListener> mOnLocationServiceSpeedChangedListeners = new CopyOnWriteArrayList<>();

    /**
     * Get the tracking state to know if {@link LocationService} is ready to start recording speed.
     *
     * @return true if ready, false otherwise
     */
    public abstract boolean isTrackingReady();

    /**
     * Get the tracking running state to know if {@link LocationService} is recording speed.
     *
     * @return true if tracking started, false otherwise
     */
    public abstract boolean isTrackingRunning();

    /**
     * Request {@link LocationService} to start recording speed.
     */
    public abstract void startTracking();

    /**
     * Request {@link LocationService} to stop recording speed.
     */
    public abstract void stopTracking();

    public abstract boolean isSpeedActive();

    /**
     * Get the average speed for the last section.
     * <p>
     * A section is defined between a starting point and a ending point.
     * Starting point is the 1st point where speed becoming active. Ending point is the last point where speed becoming fix.
     *
     * @return average speed expressed in km/h
     */
    public abstract float getAverageSpeedHistory();

    /**
     * Toggle speed record. Start tracking if tracking not starting, stop tracking if tracking is started.
     */
    public void toggleTracking() {
        if (isTrackingRunning()) {
            stopTracking();
        } else {
            startTracking();
        }
    }

    /**
     * Register a {@link OnLocationServiceStateChangedListener}.
     * <p>
     * Don't forget to unregister listener when client as finished to deal with it to avoid leak.
     *
     * @param locationServiceStateChangedListener the listener to register
     * @see #unregisterOnLocationServiceStateChangedListener(OnLocationServiceStateChangedListener)
     */
    public final void registerOnLocationServiceStateChangedListener(OnLocationServiceStateChangedListener locationServiceStateChangedListener) {
        if (mOnLocationServiceStateChangedListeners.contains(locationServiceStateChangedListener)) {
            return;
        }
        mOnLocationServiceStateChangedListeners.add(locationServiceStateChangedListener);
    }

    /**
     * Unregister a {@link OnLocationServiceStateChangedListener}.
     *
     * @param locationServiceStateChangedListener the listener to unregister
     */
    public final void unregisterOnLocationServiceStateChangedListener(OnLocationServiceStateChangedListener locationServiceStateChangedListener) {
        if (mOnLocationServiceStateChangedListeners.contains(locationServiceStateChangedListener)) {
            mOnLocationServiceStateChangedListeners.remove(locationServiceStateChangedListener);
        }
    }

    /**
     * Register a {@link OnLocationServiceSpeedChangedListener}.
     * <p>
     * Don't forget to unregister listener when client as finished to deal with it to avoid leak.
     *
     * @param onLocationServiceSpeedChangedListener the listener to register
     * @see #unregisterOnLocationServiceSpeedChangedListener(OnLocationServiceSpeedChangedListener)
     */
    public final void registerOnLocationServiceSpeedChangedListener(OnLocationServiceSpeedChangedListener onLocationServiceSpeedChangedListener) {
        if (mOnLocationServiceSpeedChangedListeners.contains(onLocationServiceSpeedChangedListener)) {
            return;
        }
        mOnLocationServiceSpeedChangedListeners.add(onLocationServiceSpeedChangedListener);
    }

    /**
     * Unregister a {@link OnLocationServiceSpeedChangedListener}.
     *
     * @param locationServiceSpeedChangedListener the listener to unregister
     */
    public final void unregisterOnLocationServiceSpeedChangedListener(OnLocationServiceSpeedChangedListener locationServiceSpeedChangedListener) {
        if (mOnLocationServiceSpeedChangedListeners.contains(locationServiceSpeedChangedListener)) {
            mOnLocationServiceSpeedChangedListeners.remove(locationServiceSpeedChangedListener);
        }
    }

    /**
     * Call it to invoke {@link OnLocationServiceStateChangedListener#onLocationServiceStateChangedListener(boolean)} on all registered listeners.
     */
    protected void notifyOnStateChanged(boolean enabled) {
        for (OnLocationServiceStateChangedListener onLocationServiceStateChangedListener : mOnLocationServiceStateChangedListeners) {
            onLocationServiceStateChangedListener.onLocationServiceStateChangedListener(enabled);
        }
    }

    /**
     * Call it to invoke {@link OnLocationServiceSpeedChangedListener#onLocationServiceSpeedChangedListener(float, Location)} on all registered listeners.
     */
    protected void notifyOnSpeedChanged(float speed, Location location) {
        for (OnLocationServiceSpeedChangedListener onLocationServiceSpeedChangedListener : mOnLocationServiceSpeedChangedListeners) {
            onLocationServiceSpeedChangedListener.onLocationServiceSpeedChangedListener(speed, location);
        }
    }

    /**
     * Call it to invoke {@link OnLocationServiceSpeedChangedListener#onLocationServiceSpeedActivityChangedListener(boolean)} on all registered listeners.
     */
    protected void notifyOnSpeedActivityChanged(boolean active) {
        for (OnLocationServiceSpeedChangedListener onLocationServiceSpeedChangedListener : mOnLocationServiceSpeedChangedListeners) {
            onLocationServiceSpeedChangedListener.onLocationServiceSpeedActivityChangedListener(active);
        }
    }
}
