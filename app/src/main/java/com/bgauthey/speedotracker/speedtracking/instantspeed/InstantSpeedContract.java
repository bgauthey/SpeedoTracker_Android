package com.bgauthey.speedotracker.speedtracking.instantspeed;

import com.bgauthey.speedotracker.BasePresenter;
import com.bgauthey.speedotracker.BaseView;

/**
 * @author bgauthey created on 08/05/2018.
 */

public interface InstantSpeedContract {

    interface View extends BaseView<Presenter> {
        void showSpeed(int speedValue);
    }

    interface Presenter extends BasePresenter{

    }
}
