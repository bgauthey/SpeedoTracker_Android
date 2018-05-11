package com.bgauthey.speedotracker.speedtracking.feedback;

import com.bgauthey.speedotracker.service.LocationProviderForTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link FeedbackPresenter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedbackPresenterTest {

    private FeedbackPresenter mPresenter;

    private LocationProviderForTest mLocationProvider;

    @Mock
    private FeedbackContract.View mMockedView;

    @Before
    public void setUp() {
        mLocationProvider = new LocationProviderForTest();

        mPresenter = new FeedbackPresenter(mMockedView, mLocationProvider);
        mPresenter.registerListener();
    }

    @After
    public void tearDown() {
        mPresenter.unregisterListener();
    }

    @Test
    public void start_showsLastAverageSpeed() {
        mPresenter.start();

        verify(mMockedView).showAverageSpeed(anyString());
    }

    @Test
    public void shouldShowAverageSpeedUpdateOnAverageSpeedChangedTriggered() {
        mLocationProvider.triggerAverageSpeedChanged(12f);
        mLocationProvider.triggerAverageSpeedChanged(8f);
        mLocationProvider.triggerAverageSpeedChanged(38f);

        verify(mMockedView, times(3)).showAverageSpeed(anyString());
    }

    @Test
    public void verifyFormatSpeedFormatter() {
        assertEquals("8,5", FeedbackPresenter.formatSpeed(8.50927f));
        assertEquals("24,6", FeedbackPresenter.formatSpeed(24.55012f));
        assertEquals("90,0", FeedbackPresenter.formatSpeed(90f));
    }
}
