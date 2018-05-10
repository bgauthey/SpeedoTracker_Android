package com.bgauthey.speedotracker.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Provides methods to help granting needed permission.
 */
public class PermissionUtils {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private PermissionUtils() {
    }

    /**
     * Check if location permission is granted. Location permission concerns
     * {@link Manifest.permission#ACCESS_FINE_LOCATION}.
     *
     * @return true if granted, false otherwise
     */
    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request location permission from an {@link Activity}. Location permission concerns
     * {@link Manifest.permission#ACCESS_FINE_LOCATION}.
     *
     * @param activity the activity requesting permission
     */
    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_LOCATION_PERMISSION);
    }

    /**
     * Extract the location permission granted result from the
     * {@link Activity#onRequestPermissionsResult(int, String[], int[])} method. Pass parameters of
     * activity method to this method.
     *
     * @return true if permission granted, false otherwise
     */
    public static boolean getLocationPermissionGrantedResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return requestCode == REQUEST_CODE_LOCATION_PERMISSION && permissions.length == 1 && grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED;
    }
}
