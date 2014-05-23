package com.akisute.yourwifi.app;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.akisute.yourwifi.app.util.GlobalSharedPreferences;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;


public class MainActivity extends DaggeredActivity implements ActionBar.TabListener {

    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    GlobalSharedPreferences mGlobalSharedPreferences;

    private EssidListFragment mEssidListFragment;
    private RawNetworkListFragment mRawNetworkListFragment;
    private NetworkMapFragment mNetworkMapFragment;

    //-------------------------------------------------------------------------
    // Activity
    //-------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEssidListFragment = new EssidListFragment();
        mRawNetworkListFragment = new RawNetworkListFragment();
        mNetworkMapFragment = new NetworkMapFragment();

        setupActionBar();
        showNetworkTransactionUsingSharedPreferences();

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
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            ActionBar.Tab tab = actionBar.getSelectedTab();
            if (tab != null) {
                outState.putInt("tab", tab.getPosition());
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt("tab", 0);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            ActionBar.Tab tab = actionBar.getTabAt(position);
            if (tab != null) {
                tab.select();
            }
        }
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
        // Not using provided transaction since there are many bugs including:
        // - Always null (http://yan-note.blogspot.jp/2012/10/android-fragmenttab.html)
        switch (tab.getPosition()) {
            case 0:
                // network tab
                showNetworkTransactionUsingSharedPreferences();
                break;
            case 1:
                // map tab
                showMapFragment();
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
        showNetworkTransactionUsingSharedPreferences();
    }

    //-------------------------------------------------------------------------
    // Fragment managements
    //-------------------------------------------------------------------------

    private void showNetworkTransactionUsingSharedPreferences() {
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
    }

    private void showRawNetworkFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, mRawNetworkListFragment);
        transaction.commit();
    }

    private void showEssidFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, mEssidListFragment);
        transaction.commit();
    }

    private void showMapFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, mNetworkMapFragment);
        transaction.commit();
    }
}
