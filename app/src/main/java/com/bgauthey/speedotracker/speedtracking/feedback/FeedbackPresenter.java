package com.bgauthey.speedotracker.speedtracking.feedback;

import com.bgauthey.speedotracker.Constants;
import com.bgauthey.speedotracker.service.LocationProvider;

import java.text.DecimalFormat;

/**
 * Gets information from the model layer and updates UI as required.
 */
public class FeedbackPresenter implements FeedbackContract.Presenter {

    private FeedbackContract.View mView;
    private LocationProvider mLocationProvider;

    public FeedbackPresenter(FeedbackContract.View view, LocationProvider locationProvider) {
        mView = view;
        mLocationProvider = locationProvider;
    }

    void registerListener() {
        mLocationProvider.registerOnLocationServiceAverageSpeedChangedListener(mAverageSpeedChangedListener);
    }

    void unregisterListener() {
        mLocationProvider.unregisterOnLocationServiceAverageSpeedChangedListener(mAverageSpeedChangedListener);
    }

    static String formatSpeed(float speed) {
        return new DecimalFormat("0.0").format(speed);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface implementation
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void start() {
        registerListener();
        float averageSpeed = mLocationProvider.getAverageSpeedHistory();
        mView.showAverageSpeed(formatSpeed(averageSpeed));
    }

    @Override
    public void stop() {
        unregisterListener();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Final implementation
    ///////////////////////////////////////////////////////////////////////////

    private final LocationProvider.OnLocationServiceAverageSpeedChangedListener mAverageSpeedChangedListener =
            new LocationProvider.OnLocationServiceAverageSpeedChangedListener() {
                @Override
                public void onLocationServiceAverageSpeedChangedListener(float averageSpeed, float distance, int timeElapsed) {
                    mView.showAverageSpeed(formatSpeed(averageSpeed));
                    if (Constants.SHOW_DEBUG_INFO) {
                        mView.showDebugInfo(averageSpeed, distance, timeElapsed);
                    }
                }
            };
}
