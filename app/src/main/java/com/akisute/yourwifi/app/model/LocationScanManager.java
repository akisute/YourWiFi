package com.akisute.yourwifi.app.model;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

public class LocationScanManager {

    @Inject
    LocationManager mLocationManager;

    @Inject
    public LocationScanManager(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    public void debug() {
        // Debug Code
        Log.d(getClass().getSimpleName(), mLocationManager.getAllProviders().toString());
    }

    private class Listener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
