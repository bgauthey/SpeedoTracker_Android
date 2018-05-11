package com.bgauthey.speedotracker.service;

import android.location.Location;
import android.os.Handler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A fake {@link LocationService} that generates a new speed every second using a {@link Handler}
 * when tracking is started.
 */
public class FakeLocationService extends LocationService {

    private static final long INTERVAL_TIME = 1000L;
    private static final float[] FAKE_SPEEDS = {8, 12, 28, 45, 60, 72, 90, 120, 70, 60, 20, 12, 0, 0};
    private static final float FAKE_AVERAGE_SPEED = 55.2f;

    private static FakeLocationService sInstance = null;

    private AtomicInteger mSpeedSelector = new AtomicInteger(0);
    private Handler mHandler;
    private boolean mTrackingEnabled = false;
    private boolean mSpeedRunning = false;

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
    public boolean isTrackingRunning() {
        return mTrackingEnabled;
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

    @Override
    public boolean isSpeedActive() {
        return mSpeedRunning;
    }

    private final Runnable mSpeedGenerator = new Runnable() {
        @Override
        public void run() {
            float speed = FAKE_SPEEDS[mSpeedSelector.getAndIncrement() % FAKE_SPEEDS.length];
            notifyOnSpeedChanged(speed, new Location(""));
            mSpeedRunning = speed != 0;
            notifyOnSpeedActivityChanged(mSpeedRunning);
            mHandler.postDelayed(this, INTERVAL_TIME);
        }
    };

}
