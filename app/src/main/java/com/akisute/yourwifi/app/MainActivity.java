package com.akisute.yourwifi.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.akisute.yourwifi.app.util.GlobalSharedPreferences;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;


public class MainActivity extends DaggeredActivity implements ActionBar.TabListener {

    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    GlobalSharedPreferences mGlobalSharedPreferences;

    //-------------------------------------------------------------------------
    // Activity
    //-------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        showNetworkTransactionUsingSharedPreferences(transaction);
        transaction.commit();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    // TabListener
    //-------------------------------------------------------------------------

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
        switch (tab.getPosition()) {
            case 0:
                // network tab
                showNetworkTransactionUsingSharedPreferences(transaction);
                break;
            case 1:
                // map tab
                showMapFragment(transaction);
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction transaction) {
        // Do nothing
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction transaction) {
        // Do nothing
    }

    //-------------------------------------------------------------------------
    // EventBus
    //-------------------------------------------------------------------------

    @Subscribe
    public void onEssidListItemSelectedEvent(EssidListFragment.OnEssidListItemSelectedEvent event) {
        EssidDetailActivity.startActivity(this, event.getEssid());
    }

    @Subscribe
    public void onNetworkListDisplayModeChangeEvent(GlobalSharedPreferences.NetworkListDisplayMode.OnChangeEvent event) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        showNetworkTransactionUsingSharedPreferences(transaction);
        transaction.commit();
    }

    //-------------------------------------------------------------------------
    // Fragment managements
    //-------------------------------------------------------------------------

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.Tab networkTab = actionBar.newTab();
            networkTab.setText(R.string.tab_network);
            networkTab.setTabListener(this);
            actionBar.addTab(networkTab, 0);

            ActionBar.Tab mapTab = actionBar.newTab();
            mapTab.setText(R.string.tab_map);
            mapTab.setTabListener(this);
            actionBar.addTab(mapTab, 1);
        }
    }

    private void showNetworkTransactionUsingSharedPreferences(FragmentTransaction transaction) {
        switch (mGlobalSharedPreferences.getNetworkListDisplayMode()) {
            case GlobalSharedPreferences.NetworkListDisplayMode.SHOW_ESSIDS:
                showEssidFragment(transaction);
                break;
            case GlobalSharedPreferences.NetworkListDisplayMode.SHOW_RAW_NETWORKS:
                showRawNetworkFragment(transaction);
                break;
            default:
                showEssidFragment(transaction);
                break;
        }
    }

    private void showRawNetworkFragment(FragmentTransaction transaction) {
        RawNetworkListFragment fragment = new RawNetworkListFragment();
        transaction.replace(R.id.fragment, fragment);
    }

    private void showEssidFragment(FragmentTransaction transaction) {
        EssidListFragment fragment = new EssidListFragment();
        transaction.replace(R.id.fragment, fragment);
    }

    private void showMapFragment(FragmentTransaction transaction) {
        MapFragment fragment = new MapFragment();
        transaction.replace(R.id.fragment, fragment);
    }
}
