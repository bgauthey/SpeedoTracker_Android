package com.bgauthey.speedotracker.speedtracking.feedback;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class FeedbackPresenter implements FeedbackContract.Presenter {

    private FeedbackContract.View mView;

    public FeedbackPresenter(FeedbackContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
