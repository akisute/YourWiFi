package com.akisute.yourwifi.app.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;

import com.akisute.android.daggered.DaggeredApplicationModule;
import com.akisute.android.daggered.ForApplication;
import com.akisute.yourwifi.app.EssidDetailActivity;
import com.akisute.yourwifi.app.EssidDetailFragment;
import com.akisute.yourwifi.app.EssidListFragment;
import com.akisute.yourwifi.app.MainActivity;
import com.akisute.yourwifi.app.NetworkRecordingService;
import com.akisute.yourwifi.app.RawNetworkListFragment;
import com.akisute.yourwifi.app.model.BssidListAdapter;
import com.akisute.yourwifi.app.model.EssidListAdapter;
import com.akisute.yourwifi.app.model.LocationScanManager;
import com.akisute.yourwifi.app.model.NetworkCache;
import com.akisute.yourwifi.app.model.NetworkScanManager;
import com.akisute.yourwifi.app.model.RawNetworkListAdapter;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.akisute.yourwifi.app.util.GlobalResources;
import com.akisute.yourwifi.app.util.GlobalSharedPreferences;

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
                EssidDetailActivity.class,
                RawNetworkListFragment.class,
                EssidListFragment.class,
                EssidDetailFragment.class,
                NetworkRecordingService.class
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
    GlobalResources provideGlobalResources(Resources resources) {
        return new GlobalResources(resources);
    }

    @Provides
    @Singleton
    GlobalSharedPreferences provideGlobalSharedPreferences(SharedPreferences sharedPreferences) {
        return new GlobalSharedPreferences(sharedPreferences);
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
    @Singleton
    LocationScanManager providesLocationScanManager(LocationManager locationManager) {
        return new LocationScanManager(locationManager);
    }

    @Provides
    RawNetworkListAdapter providesRawNetworkListAdapter(LayoutInflater layoutInflater, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        return new RawNetworkListAdapter(layoutInflater, globalEventBus, networkCache);
    }

    @Provides
    EssidListAdapter providesEssidListAdapter(LayoutInflater layoutInflater, GlobalResources globalResources, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        return new EssidListAdapter(layoutInflater, globalResources, globalEventBus, networkCache);
    }

    @Provides
    BssidListAdapter providesBssidListAdapter(LayoutInflater layoutInflater, GlobalResources globalResources) {
        return new BssidListAdapter(layoutInflater, globalResources);
    }

}
