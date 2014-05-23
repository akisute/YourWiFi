package com.akisute.yourwifi.app.model;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.akisute.yourwifi.app.util.GlobalEventBus;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

public class LocationScanManager implements LocationListener {

    @Inject
    LocationManager mLocationManager;
    @Inject
    GlobalEventBus mGlobalEventBus;

    private final SortedSet<Location> mLocations = new TreeSet<Location>(new LocationComparator());

    private boolean mScanning;

    @Inject
    public LocationScanManager(LocationManager locationManager, GlobalEventBus globalEventBus) {
        mLocationManager = locationManager;
        mGlobalEventBus = globalEventBus;
    }

    public boolean startScan() {
        mLocationManager.removeUpdates(this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000, 5.0f, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 50.0f, this);

        Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            addLocation(gpsLocation);
        }
        Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) {
            addLocation(networkLocation);
        }

        mScanning = true;
        Log.d(getClass().getSimpleName(), String.format("Started scanning."));

        return mScanning;
    }

    public void stopScan() {
        mScanning = false;
        mLocationManager.removeUpdates(this);
        Log.d(getClass().getSimpleName(), String.format("Stopped scanning."));
    }

    public boolean isScanning() {
        return mScanning;
    }

    public Location getCurrentLocation() {
        return mLocations.first();
    }

    private void addLocation(Location location) {
        mLocations.add(location);
        if (mLocations.size() > 10) {
            mLocations.remove(mLocations.last());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        addLocation(location);
        Location currentLocation = getCurrentLocation();
        if (!currentLocation.equals(location)) {
            mGlobalEventBus.postInMainThread(new OnLocationChangedEvent(currentLocation));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Do nothing
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Do nothing
    }

    private static class LocationComparator implements Comparator<Location> {
        @Override
        public int compare(Location location, Location location2) {
            // Original Logic: http://developer.android.com/guide/topics/location/strategies.html
            //
            // lesser the better
            // -1 means location is lesser = location(new) is better
            // 0 means same
            // 1 means location2 is lesser = location2(old) is better
            final int SAME = 0;
            final int LOCATION_IS_BETTER = -1;
            final int LOCATION2_IS_BETTER = 1;
            final long SIGNIFICANT_TIME_DIFF_MILLIS = 1000 * 60 * 2;
            final float SIGNIFICANT_ACCURACY_DIFF_METER = 200.0f;

            if (location == null && location2 == null) {
                return SAME;
            } else if (location == null) {
                return LOCATION2_IS_BETTER;
            } else if (location2 == null) {
                return LOCATION_IS_BETTER;
            }

            // Check whether the new location fix is newer or older
            long timeDelta = location.getTime() - location2.getTime();
            boolean isSignificantlyNewer = timeDelta > SIGNIFICANT_TIME_DIFF_MILLIS;
            boolean isSignificantlyOlder = timeDelta < -SIGNIFICANT_TIME_DIFF_MILLIS;
            boolean isNewer = timeDelta > 0;

            // If it's been more than two minutes since the current location, use the new location
            // because the user has likely moved
            if (isSignificantlyNewer) {
                return LOCATION_IS_BETTER;
                // If the new location is more than two minutes older, it must be worse
            } else if (isSignificantlyOlder) {
                return LOCATION2_IS_BETTER;
            }

            // Check whether the new location fix is more or less accurate
            float accuracyDelta = location.getAccuracy() - location2.getAccuracy();
            boolean isOldAccurate = accuracyDelta > 0;
            boolean isNewAccurate = accuracyDelta < 0;
            boolean isOldSignificantlyAccurate = accuracyDelta > SIGNIFICANT_ACCURACY_DIFF_METER;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = TextUtils.equals(location.getProvider(), location2.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isNewAccurate) {
                return LOCATION_IS_BETTER;
            } else if (isNewer && !isNewAccurate) {
                return LOCATION_IS_BETTER;
            } else if (isNewer && !isOldSignificantlyAccurate && isFromSameProvider) {
                return LOCATION_IS_BETTER;
            }
            return LOCATION2_IS_BETTER;
        }
    }

    public static class OnLocationChangedEvent {

        private final Location mLocation;

        public OnLocationChangedEvent(Location location) {
            mLocation = location;
        }

        public Location getLocation() {
            return mLocation;
        }
    }
}
