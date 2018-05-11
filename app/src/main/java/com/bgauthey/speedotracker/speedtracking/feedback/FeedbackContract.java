package com.bgauthey.speedotracker.speedtracking.feedback;

import com.bgauthey.speedotracker.BasePresenter;
import com.bgauthey.speedotracker.BaseView;

/**
 * Contract for Feedback screen displaying average speed on last section.
 */
public interface FeedbackContract {

    interface View extends BaseView<Presenter> {
        void showAverageSpeed(String averageSpeed);

        void showDebugInfo(float averageSpeed, float distance, int timeElapsed);
    }

    interface Presenter extends BasePresenter {

    }
}
