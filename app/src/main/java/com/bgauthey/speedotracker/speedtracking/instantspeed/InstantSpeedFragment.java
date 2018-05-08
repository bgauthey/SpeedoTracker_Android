package com.bgauthey.speedotracker.speedtracking.instantspeed;

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

public class InstantSpeedFragment extends Fragment implements InstantSpeedContract.View {

    private InstantSpeedContract.Presenter mPresenter;

    private TextView mTvSpeed;

    public static InstantSpeedFragment newInstance() {

        Bundle args = new Bundle();

        InstantSpeedFragment fragment = new InstantSpeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instant_speed, container, false);

        mTvSpeed = v.findViewById(R.id.tv_fis_speed);

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

    ///////////////////////////////////////////////////////////////////////////
    // Interface implementation
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void setPresenter(InstantSpeedContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showSpeed(int speedValue) {
        mTvSpeed.setText(getString(R.string.speed_label, speedValue));
    }
}
