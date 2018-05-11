package com.bgauthey.speedotracker.speedtracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bgauthey.speedotracker.Injection;
import com.bgauthey.speedotracker.R;
import com.bgauthey.speedotracker.util.PermissionUtils;

/**
 * Main activity displaying a {@link Toolbar} with a {@link FloatingActionButton} to enable/disable speed tracking.
 * Activity displays instant speed on a screen and feedback on average speed on another screen (a Bottom Sheet).
 */
public class SpeedTrackingActivity extends AppCompatActivity implements SpeedTrackingContract.View {

    private AppBarLayout mAppBarLayout;
    private FloatingActionButton mTrackingButton;
    private MenuItem mTrackingMenuItem;

    private BottomSheetBehavior mBottomSheetBehavior;

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

        mAppBarLayout = findViewById(R.id.app_bar);
        View bottomSheet = findViewById(R.id.fl_bottom_sheet_container);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Compute bottom sheet height relative to the screen height and the offset
        bottomSheet.getLayoutParams().height =
                getScreenHeight() - getResources().getDimensionPixelSize(R.dimen.bottom_sheet_height_offset);

        mTrackingButton = findViewById(R.id.fab);
        mTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTracking();
            }
        });

        // Attach a global presenter to this activity
        mPresenter = new SpeedTrackingPresenter(this, Injection.provideLocationProvider(getApplicationContext()));
        // Create speed tracking screens (Instant speed and feedback) through this controller
        SpeedTrackingController.createSpeedTrackingScreens(this);
    }

    private int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
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
            showEnableLocationAlertDialog();
        }
    }

    @Override
    public void showInstantSpeedScreen() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void showFeedbackScreen() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Displays an {@link AlertDialog} requesting user to enable device location.
     * <p>
     * By clicking positive button user will be redirected to location settings page.
     * By clicking negative button, dialog is dismissed.
     */
    private void showEnableLocationAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.speed_tracking_dialog_title)
                .setMessage(R.string.speed_tracking_dialog_message)
                .setPositiveButton(R.string.speed_tracking_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.speed_tracking_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int paramInt) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

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
