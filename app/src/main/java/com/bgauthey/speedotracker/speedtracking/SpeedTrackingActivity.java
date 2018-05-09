package com.bgauthey.speedotracker.speedtracking;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bgauthey.speedotracker.Injection;
import com.bgauthey.speedotracker.R;
import com.bgauthey.speedotracker.service.LocationService;
import com.bgauthey.speedotracker.util.PermissionUtils;

/**
 * Main activity displaying a toolbar with a button to enable/disable speed tracking.
 * Activity displays instant speed on screen and feedback on speed (average) on another screen. These
 * two screens are handled by a {@link ViewPager}.
 */
public class SpeedTrackingActivity extends AppCompatActivity implements SpeedTrackingContract.View {

    private FloatingActionButton mTrackingButton;
    private MenuItem mTrackingMenuItem;
    private ViewPager mViewPager;
    private AppBarLayout mAppBarLayout;

    private SpeedTrackingContract.Presenter mPresenter;

    private boolean mIsToolbarCollapsed = false;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_tracking);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LocationService locationService = Injection.provideLocationService(getApplicationContext());

        mPresenter = new SpeedTrackingPresenter(this, locationService);

        mAppBarLayout = findViewById(R.id.app_bar);

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
//        InstantSpeedFragment instantSpeedFragment = InstantSpeedFragment.newInstance();
//        new InstantSpeedPresenter(instantSpeedFragment, locationService);
//        FeedbackFragment feedbackFragment = FeedbackFragment.newInstance();
//        new FeedbackPresenter(feedbackFragment, locationService);

        SpeedViewPagerAdapter adapter = new SpeedViewPagerAdapter(getSupportFragmentManager(), locationService);
        mViewPager.setAdapter(adapter);

        mTrackingButton = findViewById(R.id.fab);
        mTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTracking();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_speed_tracking, menu);
        mTrackingMenuItem = menu.findItem(R.id.action_toggle_tracking);

        //Make toggle tracking anchored button disappear when toolbar is collapsed
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Show toolbar toggle recipe button only if toolbar is fully collapsed
                boolean visibility = -verticalOffset == appBarLayout.getTotalScrollRange();
                mTrackingMenuItem.setVisible(visibility);
                if (mIsToolbarCollapsed != visibility) {
                    mIsToolbarCollapsed = visibility;
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == mTrackingMenuItem.getItemId()) {
            toggleTracking();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    ///////////////////////////////////////////////////////////////////////////
    // Activity callbacks (other than lifecycle)
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.getLocationPermissionGrantedResult(requestCode, permissions, grantResults)) {
            mPresenter.startTracking();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface implementation
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void updateButtonState(boolean start) {
        updateFloatingButtonState(start);
    }

    @Override
    public void showTrackingNotReady() {
        if (!PermissionUtils.isLocationPermissionGranted(SpeedTrackingActivity.this)) {
            PermissionUtils.requestLocationPermission(SpeedTrackingActivity.this);
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
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

    ///////////////////////////////////////////////////////////////////////////
    // Class methods
    ///////////////////////////////////////////////////////////////////////////

    private void updateFloatingButtonState(boolean start) {
        @DrawableRes int res = start ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause;
        mTrackingButton.setImageResource(res);
        mTrackingMenuItem.setIcon(res);
        mAppBarLayout.setExpanded(start, true);
    }

    private void toggleTracking() {
        mPresenter.toggleTracking();
    }
}
