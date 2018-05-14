package com.bgauthey.speedotracker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bgauthey.speedotracker.service.LocationProvider;
import com.bgauthey.speedotracker.service.gps.DefaultGpsLocationCallback;
import com.bgauthey.speedotracker.service.gps.GpsLocationCallback;
import com.bgauthey.speedotracker.service.gps.GpsLocationProvider;

/**
 * Enables injection of production implementations for {@link LocationProvider}
 */
public class Injection {

    public static LocationProvider provideLocationProvider(@NonNull Context context) {
        return GpsLocationProvider.getInstance(provideLocationCallback(context));
    }

    private static GpsLocationCallback provideLocationCallback(Context context) {
        return new DefaultGpsLocationCallback(context);
    }
}
