package com.bgauthey.speedotracker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bgauthey.speedotracker.service.FakeLocationService;
import com.bgauthey.speedotracker.service.LocationService;

/**
 * Enables injection of mock implementations for {@link LocationService}
 */
public class Injection {

    public static LocationService provideLocationProvider(@NonNull Context context) {
        return FakeLocationService.getInstance();
    }
}
