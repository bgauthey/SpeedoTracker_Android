package com.bgauthey.speedotracker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bgauthey.speedotracker.service.GpsLocationService;
import com.bgauthey.speedotracker.service.LocationService;

/**
 * @author bgauthey created on 08/05/2018.
 */

public class Injection {

    public static LocationService provideLocationService(@NonNull Context context) {
        return GpsLocationService.getInstance(context);
    }
}
