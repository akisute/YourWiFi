package com.akisute.yourwifi.app.dagger;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;

import com.akisute.android.daggered.DaggeredApplicationModule;
import com.akisute.android.daggered.ForApplication;
import com.akisute.yourwifi.app.EssidListFragment;
import com.akisute.yourwifi.app.MainActivity;
import com.akisute.yourwifi.app.NetworkListFragment;
import com.akisute.yourwifi.app.model.EssidListAdapter;
import com.akisute.yourwifi.app.model.NetworkCache;
import com.akisute.yourwifi.app.model.NetworkListAdapter;
import com.akisute.yourwifi.app.model.NetworkScanManager;
import com.akisute.yourwifi.app.util.GlobalEventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DaggeredApplicationModule.class,
                AndroidSystemModule.class
        },
        injects = {
                MainActivity.class,
                NetworkListFragment.class,
                EssidListFragment.class
        }
)
public class ApplicationModule {

    @Provides
    @Singleton
    GlobalEventBus provideGlobalEventBus() {
        return new GlobalEventBus();
    }

    @Provides
    @Singleton
    NetworkCache providesNetworkCache() {
        return new NetworkCache();
    }

    @Provides
    @Singleton
    NetworkScanManager providesNetworkScanManager(@ForApplication Context context, WifiManager wifiManager, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        return new NetworkScanManager(context, wifiManager, globalEventBus, networkCache);
    }

    @Provides
    NetworkListAdapter providesNetworkListAdapter(LayoutInflater layoutInflater, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        return new NetworkListAdapter(layoutInflater, globalEventBus, networkCache);
    }

    @Provides
    EssidListAdapter providesEssidListAdapter(LayoutInflater layoutInflater, Resources resources, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        return new EssidListAdapter(layoutInflater, resources, globalEventBus, networkCache);
    }

}
