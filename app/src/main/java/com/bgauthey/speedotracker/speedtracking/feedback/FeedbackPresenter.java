package com.bgauthey.speedotracker.speedtracking.feedback;

import com.bgauthey.speedotracker.Constants;
import com.bgauthey.speedotracker.service.LocationService;

import java.text.DecimalFormat;

/**
 * Gets information from the model layer and updates UI as required.
 */
public class FeedbackPresenter implements FeedbackContract.Presenter {

    private FeedbackContract.View mView;
    private LocationService mLocationService;

    public FeedbackPresenter(FeedbackContract.View view, LocationService locationService) {
        mView = view;
        mLocationService = locationService;
    }

    void registerListener() {
        mLocationService.registerOnLocationServiceAverageSpeedChangedListener(mAverageSpeedChangedListener);
    }

    void unregisterListener() {
        mLocationService.unregisterOnLocationServiceAverageSpeedChangedListener(mAverageSpeedChangedListener);
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
        float averageSpeed = mLocationService.getAverageSpeedHistory();
        mView.showAverageSpeed(formatSpeed(averageSpeed));
    }

    @Override
    public void stop() {
        unregisterListener();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Final implementation
    ///////////////////////////////////////////////////////////////////////////

    private final LocationService.OnLocationServiceAverageSpeedChangedListener mAverageSpeedChangedListener =
            new LocationService.OnLocationServiceAverageSpeedChangedListener() {
                @Override
                public void onLocationServiceAverageSpeedChangedListener(float averageSpeed, float distance, int timeElapsed) {
                    mView.showAverageSpeed(formatSpeed(averageSpeed));
                    if (Constants.SHOW_DEBUG_INFO) {
                        mView.showDebugInfo(averageSpeed, distance, timeElapsed);
                    }
                }
            };
}
