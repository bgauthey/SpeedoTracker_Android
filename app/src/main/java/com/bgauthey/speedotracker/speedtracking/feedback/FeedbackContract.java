package com.bgauthey.speedotracker.speedtracking.feedback;

import com.bgauthey.speedotracker.BasePresenter;
import com.bgauthey.speedotracker.BaseView;

/**
 * Contract for Feedback screen displaying average speed on last section.
 */
public interface FeedbackContract {

    interface View extends BaseView<Presenter> {
        void showAverageSpeed(float averageSpeed);
    }

    interface Presenter extends BasePresenter {

    }
}
