package com.bgauthey.speedotracker.service.gps;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLocationManager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Unit tests for {@link GpsLocationProvider}
 */
@RunWith(RobolectricTestRunner.class)
public class GpsLocationProviderTest {

    private GpsLocationProvider mProvider;

    private FakeGpsLocationCallback mFakeLocationCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mFakeLocationCallback = new FakeGpsLocationCallback();

        mProvider = new GpsLocationProvider(mFakeLocationCallback);

        mFakeLocationCallback.setProviderEnabled(true); // Enable provider by default
    }

    private ShadowLocationManager getLocationManager() {
        return mFakeLocationCallback.getLocationManager();
    }

    @Test
    public void shouldReturnProviderEnabledByDefault() {
        Boolean enabled = mProvider.isTrackingReady();
        assertTrue(enabled);
    }

    @Test
    public void shouldDisableProvider() {
        mFakeLocationCallback.setProviderEnabled(false);
        assertFalse(mProvider.isTrackingReady());
    }

    @Test
    public void shouldHaveListenerOnceTrackingStarted() {
        mProvider.startTracking();
        assertEquals(1, getLocationManager().getRequestLocationUpdateListeners().size());
    }

    @Test
    public void shouldNotHaveListenerOnceTrackingStopped() {
        mProvider.startTracking();
        mProvider.stopTracking();
        assertEquals(0, getLocationManager().getRequestLocationUpdateListeners().size());
    }

    @Test
    public void shouldTrackSpeedActivityWithSuccess() {
        mProvider.startTracking();

        // Upgrade time fo more than 1 ms after previous simulation to be sure onLocationChanged() is triggered
        long time = System.currentTimeMillis();

        getLocationManager().simulateLocation(createLocation(0f, time));
        assertFalse(mProvider.isSpeedActive());

        time += 1; // add a second for next location simulation
        getLocationManager().simulateLocation(createLocation(5f, time));
        assertTrue(mProvider.isSpeedActive());

        time += 1;
        getLocationManager().simulateLocation(createLocation(20f, time));
        assertTrue(mProvider.isSpeedActive());

        time += 1;
        getLocationManager().simulateLocation(createLocation(3f, time));
        assertTrue(mProvider.isSpeedActive());

        time += 1;
        getLocationManager().simulateLocation(createLocation(GpsLocationProvider.MIN_SPEED_RUNNING, time));
        assertTrue(mProvider.isSpeedActive());

        time += 1;
        getLocationManager().simulateLocation(createLocation(GpsLocationProvider.MIN_SPEED_RUNNING - 0.01f, time));
        assertFalse(mProvider.isSpeedActive());

        mProvider.stopTracking();
    }

    private static Location createLocation(float speed, long time) {
        Location location = new Location(DefaultGpsLocationCallback.LOCATION_PROVIDER);
        location.setSpeed(speed);
        location.setTime(time);
        return location;
    }
}
