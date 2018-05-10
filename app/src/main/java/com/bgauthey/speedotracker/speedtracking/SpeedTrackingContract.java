package com.bgauthey.speedotracker.speedtracking;

import com.bgauthey.speedotracker.BasePresenter;

/**
 * Contract of screen displaying Instant Speed and Feedback screens.
 */
public interface SpeedTrackingContract {

    interface View {
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
