package com.bgauthey.speedotracker.service;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class FakeLocationService extends LocationService {

    private static final long INTERVAL_TIME = 1000L;
    private static final float[] FAKE_SPEEDS = {8, 12, 28, 45, 60, 72, 90, 120, 70, 60, 20, 12, 0, 0};
    private static final float FAKE_AVERAGE_SPEED = 55.2f;

    private static FakeLocationService sInstance = null;

    private AtomicInteger mSpeedSelector = new AtomicInteger(0);
    private boolean mTrackingEnabled = false;
    private Handler mHandler;

    private FakeLocationService() {
        mHandler = new Handler();
    }

    public static FakeLocationService getInstance() {
        if (sInstance == null) {
            sInstance = new FakeLocationService();
        }
        return sInstance;
    }

    @Override
    public boolean isTrackingReady() {
        return true;
    }

    @Override
    public boolean isTrackingStarted() {
        return mTrackingEnabled;
    }

    @Override
    public void toggleTracking() {
        if (isTrackingStarted()) {
            stopTracking();
        } else {
            startTracking();
        }
    }

    @Override
    public void startTracking() {
        mTrackingEnabled = true;
        notifyOnStateChanged(true);
        mHandler.post(mSpeedGenerator);
    }

    @Override
    public void stopTracking() {
        mTrackingEnabled = false;
        mHandler.removeCallbacks(mSpeedGenerator);
        notifyOnStateChanged(false);
    }

    @Override
    public float getAverageSpeedHistory() {
        return FAKE_AVERAGE_SPEED;
    }

    private final Runnable mSpeedGenerator = new Runnable() {
        @Override
        public void run() {
            float speed = FAKE_SPEEDS[mSpeedSelector.getAndIncrement() % FAKE_SPEEDS.length];
            notifyOnSpeedChanged(speed);
            if (speed == 0) {
                notifyOnSpeedActivityChanged(false);
            } else {
                notifyOnSpeedActivityChanged(true);
            }
            mHandler.postDelayed(this, INTERVAL_TIME);
        }
    };

}
