package com.bgauthey.speedotracker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bgauthey.speedotracker.service.GpsLocationProvider;
import com.bgauthey.speedotracker.service.LocationService;

/**
 * Enables injection of production implementations for {@link LocationService}
 */
public class Injection {

    public static LocationService provideLocationProvider(@NonNull Context context) {
        return GpsLocationProvider.getInstance(context);
    }
}
