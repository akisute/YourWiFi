package com.akisute.yourwifi.app;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akisute.android.daggered.DaggeredFragment;
import com.akisute.yourwifi.app.model.LocationScanManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NetworkMapFragment extends DaggeredFragment implements GoogleMap.OnMyLocationButtonClickListener {

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
        Log.d("Godfuck", String.format("onCreateView for NetworkMapFragment %s", this));
        View view = inflater.inflate(R.layout.fragment_network_map, container, false);
        ButterKnife.inject(this, view);

        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity()); // Required before CameraUpdateFactory usage: http://stackoverflow.com/questions/19541915/google-maps-cameraupdatefactory-not-initalized
        mMap = mMapView.getMap();
        if (mMap != null) {
            // Google Map is available, lets use it
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            restoreCurrentMapCameraPosition(savedInstanceState);
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
        saveCurrentMapCameraPosition(outState);
    }

    //-------------------------------------------------------------------------
    // GoogleMap Listeners
    //-------------------------------------------------------------------------

    private void saveCurrentMapCameraPosition(@NotNull Bundle outState) {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        outState.putParcelable("cameraPosition", cameraPosition);
    }

    private void restoreCurrentMapCameraPosition(@Nullable Bundle savedInstanceState) {
        CameraPosition cameraPosition = null;
        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable("cameraPosition");
        }
        if (cameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            setMapCameraPositionToCurrentLocation(false);
        }
    }

    private void setMapCameraPositionToCurrentLocation(boolean animated) {
        Location currentLocation = mLocationScanManager.getCurrentLocation();
        LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (animated) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        setMapCameraPositionToCurrentLocation(true);
        return true;
    }
}
