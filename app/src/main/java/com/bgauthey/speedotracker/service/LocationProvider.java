package com.bgauthey.speedotracker.service;

import android.location.Location;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A base to create a provider handling location and speed.
 * <p>
 * It provides methods to notify about {@link LocationProvider} states.
 * <p>
 * {@link OnSpeedRecordingStateChangedListener} gives feedback about speed recording state.
 * <p>
 * {@link OnSpeedChangedListener} gives feedback about speed changed.
 * <p>
 * {@link OnAverageSpeedChangedListener} gives feedback about average speed changed.
 */
public abstract class LocationProvider {

    /**
     * Interface definition for a callback to be invoked when speed recording state changed.
     */
    public interface OnSpeedRecordingStateChangedListener {
        /**
         * Invoked when speed recording state changed.
         *
         * @param enabled true if under recording, false otherwise
         */
        void onSpeedRecordingStateChanged(boolean enabled);
    }

    /**
     * Interface definition for a callback to be invoked when speed changed.
     */
    public interface OnSpeedChangedListener {
        /**
         * Invoked when the location is updated.
         *
         * @param speed    speed computed for this location
         * @param location location where speed is computed
         */
        void onSpeedChanged(float speed, Location location);

        /**
         * Invoked when speed activity changed. Speed is considered active when greater than 0 and inactive when equal to 0.
         *
         * @param active true if speed is active, false otherwise.
         */
        void onSpeedActivityChanged(boolean active);
    }

    /**
     * Interface definition for a callback to be invoked when average speed changed.
     */
    public interface OnAverageSpeedChangedListener {
        /**
         * Invoked when the average speed is updated.
         *
         * @param averageSpeed average speed for the section.
         */
        void onAverageSpeedChanged(float averageSpeed, float distance, int timeElapsed);

    }

    private CopyOnWriteArrayList<OnSpeedRecordingStateChangedListener> mOnSpeedRecordingStateChangedListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<OnSpeedChangedListener> mOnSpeedChangedListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<OnAverageSpeedChangedListener> mOnAverageSpeedChangedListeners = new CopyOnWriteArrayList<>();

    /**
     * Get the tracking state to know if {@link LocationProvider} is ready to start recording speed.
     *
     * @return true if ready, false otherwise
     */
    public abstract boolean isTrackingReady();

    /**
     * Get the tracking running state to know if {@link LocationProvider} is recording speed.
     *
     * @return true if tracking started, false otherwise
     */
    public abstract boolean isTrackingRunning();

    /**
     * Request {@link LocationProvider} to start recording speed.
     */
    public abstract void startTracking();

    /**
     * Request {@link LocationProvider} to stop recording speed.
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
     * Register a {@link OnSpeedRecordingStateChangedListener}.
     * <p>
     * Don't forget to unregister listener when client as finished to deal with it to avoid leak.
     *
     * @param locationServiceStateChangedListener the listener to register
     * @see #unregisterOnLocationServiceStateChangedListener(OnSpeedRecordingStateChangedListener)
     */
    public final void registerOnLocationServiceStateChangedListener(OnSpeedRecordingStateChangedListener locationServiceStateChangedListener) {
        if (mOnSpeedRecordingStateChangedListeners.contains(locationServiceStateChangedListener)) {
            return;
        }
        mOnSpeedRecordingStateChangedListeners.add(locationServiceStateChangedListener);
    }

    /**
     * Unregister a {@link OnSpeedRecordingStateChangedListener}.
     *
     * @param locationServiceStateChangedListener the listener to unregister
     */
    public final void unregisterOnLocationServiceStateChangedListener(OnSpeedRecordingStateChangedListener locationServiceStateChangedListener) {
        if (mOnSpeedRecordingStateChangedListeners.contains(locationServiceStateChangedListener)) {
            mOnSpeedRecordingStateChangedListeners.remove(locationServiceStateChangedListener);
        }
    }

    /**
     * Register a {@link OnSpeedChangedListener}.
     * <p>
     * Don't forget to unregister listener when client as finished to deal with it to avoid leak.
     *
     * @param onSpeedChangedListener the listener to register
     * @see #unregisterOnLocationServiceSpeedChangedListener(OnSpeedChangedListener)
     */
    public final void registerOnLocationServiceSpeedChangedListener(OnSpeedChangedListener onSpeedChangedListener) {
        if (mOnSpeedChangedListeners.contains(onSpeedChangedListener)) {
            return;
        }
        mOnSpeedChangedListeners.add(onSpeedChangedListener);
    }

    /**
     * Unregister a {@link OnSpeedChangedListener}.
     *
     * @param locationServiceSpeedChangedListener the listener to unregister
     */
    public final void unregisterOnLocationServiceSpeedChangedListener(OnSpeedChangedListener locationServiceSpeedChangedListener) {
        if (mOnSpeedChangedListeners.contains(locationServiceSpeedChangedListener)) {
            mOnSpeedChangedListeners.remove(locationServiceSpeedChangedListener);
        }
    }

    /**
     * Register a {@link OnAverageSpeedChangedListener}.
     * <p>
     * Don't forget to unregister listener when client as finished to deal with it to avoid leak.
     *
     * @param onAverageSpeedChangedListener the listener to register
     * @see #unregisterOnLocationServiceAverageSpeedChangedListener(OnAverageSpeedChangedListener)
     */
    public final void registerOnLocationServiceAverageSpeedChangedListener(OnAverageSpeedChangedListener onAverageSpeedChangedListener) {
        if (mOnAverageSpeedChangedListeners.contains(onAverageSpeedChangedListener)) {
            return;
        }
        mOnAverageSpeedChangedListeners.add(onAverageSpeedChangedListener);
    }

    /**
     * Unregister a {@link OnAverageSpeedChangedListener}.
     *
     * @param locationServiceAverageSpeedChangedListener the listener to unregister
     */
    public final void unregisterOnLocationServiceAverageSpeedChangedListener(OnAverageSpeedChangedListener locationServiceAverageSpeedChangedListener) {
        if (mOnAverageSpeedChangedListeners.contains(locationServiceAverageSpeedChangedListener)) {
            mOnAverageSpeedChangedListeners.remove(locationServiceAverageSpeedChangedListener);
        }
    }

    /**
     * Call it to invoke {@link OnSpeedRecordingStateChangedListener#onSpeedRecordingStateChanged(boolean)} on all registered listeners.
     */
    protected void notifyOnStateChanged(boolean enabled) {
        for (OnSpeedRecordingStateChangedListener onSpeedRecordingStateChangedListener : mOnSpeedRecordingStateChangedListeners) {
            onSpeedRecordingStateChangedListener.onSpeedRecordingStateChanged(enabled);
        }
    }

    /**
     * Call it to invoke {@link OnSpeedChangedListener#onSpeedChanged(float, Location)} on all registered listeners.
     */
    protected void notifyOnSpeedChanged(float speed, Location location) {
        for (OnSpeedChangedListener onSpeedChangedListener : mOnSpeedChangedListeners) {
            onSpeedChangedListener.onSpeedChanged(speed, location);
        }
    }

    /**
     * Call it to invoke {@link OnSpeedChangedListener#onSpeedActivityChanged(boolean)} on all registered listeners.
     */
    protected void notifyOnSpeedActivityChanged(boolean active) {
        for (OnSpeedChangedListener onSpeedChangedListener : mOnSpeedChangedListeners) {
            onSpeedChangedListener.onSpeedActivityChanged(active);
        }
    }

    /**
     * Call it to invoke {@link OnAverageSpeedChangedListener#onAverageSpeedChanged(float, float, int)} on all registered listeners.
     */
    protected void notifyOnAverageSpeedChanged(float averageSpeed, float distance, int timeElapsed) {
        for (OnAverageSpeedChangedListener onAverageSpeedChangedListener : mOnAverageSpeedChangedListeners) {
            onAverageSpeedChangedListener.onAverageSpeedChanged(averageSpeed, distance, timeElapsed);
        }
    }
}
