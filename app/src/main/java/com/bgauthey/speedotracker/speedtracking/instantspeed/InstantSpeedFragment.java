package com.bgauthey.speedotracker.speedtracking.instantspeed;

import android.annotation.SuppressLint;
import android.location.Location;
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
 * Displays instant speed in a {@link TextView}
 */
public class InstantSpeedFragment extends Fragment implements InstantSpeedContract.View {

    private InstantSpeedContract.Presenter mPresenter;

    private TextView mTvSpeed;
    private TextView mTvDebug;

    public static InstantSpeedFragment newInstance() {

        Bundle args = new Bundle();

        InstantSpeedFragment fragment = new InstantSpeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fragment lifecycle
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instant_speed, container, false);

        mTvSpeed = v.findViewById(R.id.tv_fis_speed);
        mTvDebug = v.findViewById(R.id.tv_location_debug);

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
    public void showSpeed(String speedValue) {
        mTvSpeed.setText(getString(R.string.speed_label, speedValue));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showLocationDebug(Location location) {
        mTvDebug.setVisibility(View.VISIBLE);
        mTvDebug.setText("Location: speed=" + location.getSpeed() + ", long=" + location.getLongitude()
                + ", lat=" + location.getLatitude() + ", accuracy=" + location.getAccuracy()
                + ", time=" + location.getTime());
    }
}
