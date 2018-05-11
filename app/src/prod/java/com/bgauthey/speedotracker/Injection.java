package com.bgauthey.speedotracker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bgauthey.speedotracker.service.LocationService;
import com.bgauthey.speedotracker.service.gps.DefaultGpsLocationCallback;
import com.bgauthey.speedotracker.service.gps.GpsLocationCallback;
import com.bgauthey.speedotracker.service.gps.GpsLocationProvider;

/**
 * Enables injection of production implementations for {@link LocationService}
 */
public class Injection {

    public static LocationService provideLocationProvider(@NonNull Context context) {
        return GpsLocationProvider.getInstance(provideLocationCallback(context));
    }

    private static GpsLocationCallback provideLocationCallback(Context context) {
        return new DefaultGpsLocationCallback(context);
    }
}
