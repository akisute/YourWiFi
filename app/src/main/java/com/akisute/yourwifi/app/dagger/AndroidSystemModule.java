package com.akisute.yourwifi.app.dagger;

import android.content.Context;
import android.content.res.Resources;
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
    WifiManager provideWifiManager(@ForApplication Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Provides
    LayoutInflater provideLayoutInflater(@ForInjecting Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
