package com.akisute.yourwifi.app.dagger;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;

import com.akisute.android.daggered.DaggeredApplicationModule;
import com.akisute.android.daggered.ForApplication;
import com.akisute.android.daggered.ForInjecting;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DaggeredApplicationModule.class
        },
        library = true
)
public class AndroidSystemModule {

    @Provides
    @Singleton
    Resources provideResources(@ForApplication Context context) {
        return context.getResources();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(@ForApplication Context context) {
        return context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    WifiManager provideWifiManager(@ForApplication Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Provides
    @Singleton
    LocationManager provideLocationManager(@ForApplication Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(@ForApplication Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    LayoutInflater provideLayoutInflater(@ForInjecting Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
