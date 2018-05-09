package com.bgauthey.speedotracker.speedtracking.feedback;

import com.bgauthey.speedotracker.service.LocationService;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class FeedbackPresenter implements FeedbackContract.Presenter {

    private FeedbackContract.View mView;
    private LocationService mLocationService;

    public FeedbackPresenter(FeedbackContract.View view, LocationService locationService) {
        mView = view;
        mView.setPresenter(this);
        mLocationService = locationService;
    }

    @Override
    public void start() {
        float averageSpeed = mLocationService.getAverageSpeedHistory();
        mView.showAverageSpeed(averageSpeed);
    }

    @Override
    public void stop() {
        // Nothing to do
    }
}
