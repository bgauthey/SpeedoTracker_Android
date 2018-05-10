package com.bgauthey.speedotracker.speedtracking.instantspeed;

import android.location.Location;

import com.bgauthey.speedotracker.BasePresenter;
import com.bgauthey.speedotracker.BaseView;

/**
 * Contract of the screen displaying instant speed.
 */
public interface InstantSpeedContract {

    interface View extends BaseView<Presenter> {
        void showSpeed(int speedValue);

        void showLocationDebug(Location location);
    }

    interface Presenter extends BasePresenter{

    }
}
