package com.bgauthey.speedotracker.speedtracking;

import com.bgauthey.speedotracker.BasePresenter;
import com.bgauthey.speedotracker.BaseView;

/**
 * @author bgauthey created on 08/05/2018.
 */

public interface SpeedTrackingContract {

    interface View extends BaseView<Presenter> {
        void updateButtonState(boolean start);

        void showTrackingNotReady();

        void showInstantSpeedScreen();

        void showFeedbackScreen();
    }

    interface Presenter extends BasePresenter {
        void toggleTracking();

        void startTracking();
    }
}
