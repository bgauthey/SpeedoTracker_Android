package com.bgauthey.speedotracker.speedtracking.feedback;

import com.bgauthey.speedotracker.BasePresenter;
import com.bgauthey.speedotracker.BaseView;

/**
 * @author bgauthey created on 08/05/2018.
 */

public interface FeedbackContract {

    interface View extends BaseView<Presenter> {
        void showAverageSpeed(float averageSpeed);
    }

    interface Presenter extends BasePresenter {

    }
}
