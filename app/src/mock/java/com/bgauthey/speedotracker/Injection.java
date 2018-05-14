package com.bgauthey.speedotracker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bgauthey.speedotracker.service.FakeLocationService;
import com.bgauthey.speedotracker.service.LocationProvider;


/**
 * Enables injection of mock implementations for {@link LocationProvider}
 */
public class Injection {

    public static LocationProvider provideLocationProvider(@NonNull Context context) {
        return FakeLocationService.getInstance();
    }
}
