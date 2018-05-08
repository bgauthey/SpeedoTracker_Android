package com.bgauthey.speedotracker.speedtracking;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bgauthey.speedotracker.Injection;
import com.bgauthey.speedotracker.R;
import com.bgauthey.speedotracker.service.LocationService;
import com.bgauthey.speedotracker.util.PermissionUtils;

public class SpeedTrackingActivity extends AppCompatActivity implements SpeedTrackingContract.View {

    private FloatingActionButton mTrackingButton;
    private ViewPager mViewPager;

    private SpeedTrackingContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_tracking);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LocationService locationService = Injection.provideLocationService(getApplicationContext());

        mPresenter = new SpeedTrackingPresenter(this, locationService);

//        InstantSpeedFragment instantSpeedFragment =
//                (InstantSpeedFragment) getSupportFragmentManager().findFragmentById(R.id.fl_fragment_container);
//
//        if (instantSpeedFragment == null) {
//            instantSpeedFragment = InstantSpeedFragment.newInstance();
//
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), instantSpeedFragment, R.id.fl_fragment_container);
//        }
//        new InstantSpeedPresenter(instantSpeedFragment, locationService);
//
//        FeedbackFragment feedbackFragment =
//                (FeedbackFragment) getSupportFragmentManager().findFragmentById(R.id.fl_bottom_sheet_container);
//
//        if (feedbackFragment == null) {
//            feedbackFragment = FeedbackFragment.newInstance();
//
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), feedbackFragment, R.id.fl_bottom_sheet_container);
//        }
//        new FeedbackPresenter(feedbackFragment);

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(new SpeedViewPagerAdapter(getSupportFragmentManager(), locationService));
        mViewPager.setCurrentItem(0);

        mTrackingButton = findViewById(R.id.fab);
        mTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionUtils.isLocationPermissionGranted(SpeedTrackingActivity.this)) {
                    PermissionUtils.requestLocationPermission(SpeedTrackingActivity.this);
                } else if (!locationService.isTrackingReady()){
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } else {
                    locationService.toggleTracking();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stop();
    }

    @Override
    public void updateButtonState(boolean start) {
        updateFloatingButtonState(start);
    }

    @Override
    public void showInstantSpeedScreen() {
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void showFeedbackScreen() {
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void setPresenter(SpeedTrackingContract.Presenter presenter) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.getLocationPermissionGrantedResult(requestCode, permissions, grantResults)) {
            mPresenter.startTracking();
        }
    }

    private void updateFloatingButtonState(boolean start) {
        mTrackingButton.setImageResource(start ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
    }

    private void toggleFragments() {
        mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % 2);
    }
}
