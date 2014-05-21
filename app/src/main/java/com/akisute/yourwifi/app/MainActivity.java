package com.akisute.yourwifi.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.akisute.yourwifi.app.util.GlobalSharedPreferences;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;


public class MainActivity extends DaggeredActivity {

    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    GlobalSharedPreferences mGlobalSharedPreferences;

    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch (mGlobalSharedPreferences.getNetworkListDisplayMode()) {
            case GlobalSharedPreferences.NetworkListDisplayMode.SHOW_ESSIDS:
                showEssidFragment();
                break;
            case GlobalSharedPreferences.NetworkListDisplayMode.SHOW_RAW_NETWORKS:
                showRawNetworkFragment();
                break;
            default:
                showEssidFragment();
                break;
        }

        mGlobalEventBus.register(this);
        NetworkRecordingService.start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGlobalEventBus.unregister(this);
        // Do not stop NetworkRecordingService to keep it running background. It will be stopped from Notification Manager.
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuActionShowRawNetworks = menu.findItem(R.id.action_show_raw_networks);
        MenuItem menuActionShowEssids = menu.findItem(R.id.action_show_essids);

        if (mCurrentFragment instanceof RawNetworkListFragment) {
            menuActionShowRawNetworks.setVisible(false);
            menuActionShowEssids.setVisible(true);
        } else if (mCurrentFragment instanceof EssidListFragment) {
            menuActionShowRawNetworks.setVisible(true);
            menuActionShowEssids.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_raw_networks:
                showRawNetworkFragment();
                return true;
            case R.id.action_show_essids:
                showEssidFragment();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEssidSelectedEvent(EssidListFragment.OnEssidSelectedEvent event) {
        EssidDetailActivity.startActivity(this, event.getEssid());
    }

    private void showRawNetworkFragment() {
        RawNetworkListFragment fragment = new RawNetworkListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        mCurrentFragment = fragment;
        mGlobalSharedPreferences.setNetworkListDisplayMode(GlobalSharedPreferences.NetworkListDisplayMode.SHOW_RAW_NETWORKS);
    }

    private void showEssidFragment() {
        EssidListFragment fragment = new EssidListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        mCurrentFragment = fragment;
        mGlobalSharedPreferences.setNetworkListDisplayMode(GlobalSharedPreferences.NetworkListDisplayMode.SHOW_ESSIDS);
    }
}
