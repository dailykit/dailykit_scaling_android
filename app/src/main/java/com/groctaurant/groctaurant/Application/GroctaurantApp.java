package com.groctaurant.groctaurant.Application;

import android.app.Application;

import com.bugfender.sdk.Bugfender;
import com.groctaurant.groctaurant.BuildConfig;

/**
 * Created by Danish Rafique on 13-08-2018.
 */
public class GroctaurantApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Bugfender.init(this, "W1cec16qqbciPmAel2f9srx2ch8Oei59", BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableLogcatLogging();
    }
}
