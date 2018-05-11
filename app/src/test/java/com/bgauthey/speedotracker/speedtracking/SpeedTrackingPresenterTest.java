package com.bgauthey.speedotracker.speedtracking;

import com.bgauthey.speedotracker.service.LocationProviderForTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SpeedTrackingPresenter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SpeedTrackingPresenterTest {

    private SpeedTrackingPresenter mPresenter;

    @Mock
    private SpeedTrackingContract.View mMockedView;

    private LocationProviderForTest mMockedLocationProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mMockedLocationProvider = new LocationProviderForTest();

        mPresenter = new SpeedTrackingPresenter(mMockedView, mMockedLocationProvider);
        mPresenter.registerListeners();
    }

    @After
    public void tearDown() {
        mPresenter.unregisterListeners();
    }

    @Test
    public void start_speedActive_showsInstantSpeedScreen() {
        mPresenter.start();

        mMockedLocationProvider.triggerStateChanged(true);

        verify(mMockedView).showInstantSpeedScreen();
    }

    @Test
    public void start_speedInactive_showsInstantSpeedScreen() {
        mPresenter.start();

        mMockedLocationProvider.triggerStateChanged(false);

        // Feedback screen requested to be shown 2 times. One at start, one after trigger
        verify(mMockedView, times(2)).showFeedbackScreen();
    }

    @Test
    public void toggleTracking_trackingDisabled_showsTrackingNotReady() {
        mPresenter.toggleTracking();

        verify(mMockedView).showTrackingNotReady();
    }

    @Test
    public void toggleTracking_stopTracking_showsTrackingNotReady() {
        mMockedLocationProvider.setTrackingReady(true);
        mMockedLocationProvider.setTrackingRunning(true);

        mPresenter.toggleTracking();

        verify(mMockedView, never()).showTrackingNotReady();
        verify(mMockedView).showFeedbackScreen();
        verify(mMockedView).updateButtonState(anyBoolean());
    }

    @Test
    public void toggleTracking_startTracking_showsTrackingNotReady() {
        mMockedLocationProvider.setTrackingReady(true);
        mMockedLocationProvider.setTrackingRunning(false);

        mPresenter.toggleTracking();

        verify(mMockedView).showInstantSpeedScreen();
        verify(mMockedView).updateButtonState(anyBoolean());
    }

    @Test
    public void shouldShowScreensSeq1InOrderWhenActivityChangedTriggered() {
        mMockedLocationProvider.triggerSpeedActivityChanged(true);
        mMockedLocationProvider.triggerSpeedActivityChanged(false);

        InOrder orderVerify = inOrder(mMockedView);

        orderVerify.verify(mMockedView).showInstantSpeedScreen();
        orderVerify.verify(mMockedView).showFeedbackScreen();
    }

    @Test
    public void shouldShowScreensSeq2InOrderWhenActivityChangedTriggered() {
        mMockedLocationProvider.triggerSpeedActivityChanged(false);
        mMockedLocationProvider.triggerSpeedActivityChanged(true);

        InOrder orderVerify = inOrder(mMockedView);

        orderVerify.verify(mMockedView).showFeedbackScreen();
        orderVerify.verify(mMockedView).showInstantSpeedScreen();
    }
}
