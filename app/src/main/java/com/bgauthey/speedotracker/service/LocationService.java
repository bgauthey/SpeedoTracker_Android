package com.bgauthey.speedotracker.service;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bgauthey created on 08/05/2018.
 */

public abstract class LocationService {

    public interface OnLocationServiceStateChangedListener {
        void onLocationServiceStateChangedListener(boolean enabled);
    }

    public interface OnLocationServiceSpeedChangedListener {
        void onLocationServiceSpeedChangedListener(float speed);
    }

    private CopyOnWriteArrayList<OnLocationServiceStateChangedListener> mOnLocationServiceStateChangedListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<OnLocationServiceSpeedChangedListener> mOnLocationServiceSpeedChangedListeners = new CopyOnWriteArrayList<>();

    public abstract boolean isTrackingReady();

    public abstract boolean isTrackingStarted();

    public abstract void toggleTracking();

    public abstract void startTracking();

    public abstract void stopTracking();

    public final void registerOnLocationServiceStateChangedListener(OnLocationServiceStateChangedListener locationServiceStateChangedListener) {
        if (mOnLocationServiceStateChangedListeners.contains(locationServiceStateChangedListener)) {
            return;
        }
        mOnLocationServiceStateChangedListeners.add(locationServiceStateChangedListener);
    }

    public final void unregisterOnLocationServiceStateChangedListener(OnLocationServiceStateChangedListener locationServiceStateChangedListener) {
        if (mOnLocationServiceStateChangedListeners.contains(locationServiceStateChangedListener)) {
            mOnLocationServiceStateChangedListeners.remove(locationServiceStateChangedListener);
        }
    }

    public final void registerOnLocationServiceSpeedChangedListener(OnLocationServiceSpeedChangedListener onLocationServiceSpeedChangedListener) {
        if (mOnLocationServiceSpeedChangedListeners.contains(onLocationServiceSpeedChangedListener)) {
            return;
        }
        mOnLocationServiceSpeedChangedListeners.add(onLocationServiceSpeedChangedListener);
    }

    public final void unregisterOnLocationServiceSpeedChangedListener(OnLocationServiceSpeedChangedListener locationServiceSpeedChangedListener) {
        if (mOnLocationServiceSpeedChangedListeners.contains(locationServiceSpeedChangedListener)) {
            mOnLocationServiceSpeedChangedListeners.remove(locationServiceSpeedChangedListener);
        }
    }

    protected void notifyOnStateChanged(boolean enabled) {
        for (OnLocationServiceStateChangedListener onLocationServiceStateChangedListener : mOnLocationServiceStateChangedListeners) {
            onLocationServiceStateChangedListener.onLocationServiceStateChangedListener(enabled);
        }
    }

    protected void notifyOnSpeedChanged(float speed) {
        for (OnLocationServiceSpeedChangedListener onLocationServiceSpeedChangedListener : mOnLocationServiceSpeedChangedListeners) {
            onLocationServiceSpeedChangedListener.onLocationServiceSpeedChangedListener(speed);
        }
    }
}
