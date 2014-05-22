package com.akisute.yourwifi.app;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akisute.android.daggered.DaggeredFragment;
import com.akisute.yourwifi.app.model.LocationScanManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MapFragment extends DaggeredFragment implements GoogleMap.OnMyLocationButtonClickListener {

    @Inject
    LocationScanManager mLocationScanManager;
    @InjectView(R.id.mapView)
    MapView mMapView;

    private GoogleMap mMap;

    //-------------------------------------------------------------------------
    // Fragment
    //-------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.inject(this, view);

        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity()); // Required before CameraUpdateFactory usage: http://stackoverflow.com/questions/19541915/google-maps-cameraupdatefactory-not-initalized
        mMap = mMapView.getMap();
        if (mMap != null) {
            // Google Map is available, lets use it
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            setMapCameraPositionToCurrentLocation(false);
        } else {
            // Google Map is not available, just hide the map and display labels to explain
            mMapView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    //-------------------------------------------------------------------------
    // GoogleMap Listeners
    //-------------------------------------------------------------------------

    private void setMapCameraPositionToCurrentLocation(boolean animated) {
        Location currentLocation = mLocationScanManager.getCurrentLocation();
        LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (animated) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    private void setMapCameraPositionToCurrentLocationBounds(boolean animated) {
        // Convert from distance(km) to latitude and longitude for current location, then use it as a boundary
        // http://www.movable-type.co.uk/scripts/latlong.html

        // XXX: This method doesn't work well in onCreateView/onResume since CameraUpdateFactory.newLatLngBounds() requires view bounds to be calculated properly

        // TODO: calculation is broken :(
        Location currentLocation = mLocationScanManager.getCurrentLocation();
        double lat1 = currentLocation.getLatitude();
        double lng1 = currentLocation.getLongitude();
        double d = currentLocation.getAccuracy() * 1.5;
        final double R = 6371 * 1000;
        double dR = d / R;
        final double bearingNorth = 0;
        double latNorth = Math.asin(Math.sin(lat1) * Math.cos(dR) + Math.cos(lat1) * Math.sin(dR) * Math.cos(bearingNorth));
        final double bearingEast = Math.PI / 2;
        double latEast = Math.asin(Math.sin(lat1) * Math.cos(dR) + Math.cos(lat1) * Math.sin(dR) * Math.cos(bearingEast));
        double lngEast = lng1 + Math.atan2(Math.sin(bearingEast) * Math.sin(dR) * Math.cos(lat1), Math.cos(dR) - Math.sin(lat1) * Math.sin(latEast));
        double deltaNorth = Math.abs(latNorth - lat1);
        double deltaEast = Math.abs(lngEast - lng1);
        LatLng southwest = new LatLng(currentLocation.getLatitude() - deltaNorth * 2, currentLocation.getLongitude() - deltaEast * 2);
        LatLng northeast = new LatLng(currentLocation.getLatitude() + deltaNorth * 2, currentLocation.getLongitude() + deltaEast * 2);
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        if (animated) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        setMapCameraPositionToCurrentLocation(true);
        return true;
    }
}
