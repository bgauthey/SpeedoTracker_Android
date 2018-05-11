package com.bgauthey.speedotracker.service.gps;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLocationManager;

import static org.robolectric.Shadows.shadowOf;

/**
 * A fake {@link GpsLocationCallback} used for testing. The {@link LocationManager} is mocked
 * using {@link ShadowLocationManager} of Robolectric library
 */
public class FakeGpsLocationCallback implements GpsLocationCallback {

    private static final String LOCATION_PROVIDER = DefaultGpsLocationCallback.LOCATION_PROVIDER;

    private ShadowLocationManager mShadowLocationManager;
    private LocationManager mLocationManager;

    public FakeGpsLocationCallback() {
        mLocationManager = (LocationManager) RuntimeEnvironment.application.getSystemService(Context.LOCATION_SERVICE);
        mShadowLocationManager = shadowOf(mLocationManager);
    }

    public ShadowLocationManager getLocationManager() {
        return mShadowLocationManager;
    }

    @Override
    public boolean isTrackingReady() {
        return mLocationManager.isProviderEnabled(LOCATION_PROVIDER);
    }

    @Override
    public void startTracking(LocationListener listener) {
        mShadowLocationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, listener);
    }

    @Override
    public void stopTracking(LocationListener listener) {
        mShadowLocationManager.removeUpdates(listener);
    }

    public void setProviderEnabled(boolean enable) {
        mShadowLocationManager.setProviderEnabled(LOCATION_PROVIDER, enable);
    }
}
