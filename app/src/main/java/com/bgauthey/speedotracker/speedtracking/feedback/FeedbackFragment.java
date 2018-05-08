package com.bgauthey.speedotracker.speedtracking.feedback;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bgauthey.speedotracker.R;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class FeedbackFragment extends Fragment implements FeedbackContract.View {

    private FeedbackContract.Presenter mPresenter;

    private TextView mTvAverageSpeed;

    public static FeedbackFragment newInstance() {

        Bundle args = new Bundle();

        FeedbackFragment fragment = new FeedbackFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback_speed, container, false);

        mTvAverageSpeed = v.findViewById(R.id.tv_ff_speed_value);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stop();
    }

    @Override
    public void showAverageSpeed(float averageSpeed) {

    }

    @Override
    public void setPresenter(FeedbackContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
