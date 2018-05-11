package com.bgauthey.speedotracker.speedtracking.instantspeed;

import com.bgauthey.speedotracker.service.LocationProviderForTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link InstantSpeedPresenter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstantSpeedPresenterTest {

    private InstantSpeedPresenter mPresenter;

    private LocationProviderForTest mLocationProvider;

    @Mock
    private InstantSpeedContract.View mMockedView;

    @Before
    public void setUp() {
        mLocationProvider = new LocationProviderForTest();
        mPresenter = new InstantSpeedPresenter(mMockedView, mLocationProvider);

        mPresenter.start();
    }

    @After
    public void tearDown() {
        mPresenter.stop();
    }

    @Test
    public void shouldShowSpeedUpdateOnSpeedChangedTriggered() {
        mLocationProvider.triggerSpeedChanged(5f);
        mLocationProvider.triggerSpeedChanged(20f);
        mLocationProvider.triggerSpeedChanged(30f);
        mLocationProvider.triggerSpeedChanged(0f);

        verify(mMockedView, times(4)).showSpeed(anyString());
    }

    @Test
    public void shouldShowSpeedWithoutPrecisionAbove() {
        mLocationProvider.triggerSpeedChanged(5.8965f);
        verify(mMockedView).showSpeed(eq("6"));
    }

    @Test
    public void shouldShowSpeedWithoutPrecisionBelow() {
        mLocationProvider.triggerSpeedChanged(12.4532f);
        verify(mMockedView).showSpeed(eq("12"));
    }
}
