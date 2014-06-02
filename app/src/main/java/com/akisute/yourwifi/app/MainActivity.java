package com.akisute.yourwifi.app;

import android.app.ActionBar;
import android.app.Fragment;
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
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;


public class MainActivity extends DaggeredActivity implements ActionBar.TabListener {

    public static final String STATE_SELECTED_TAB_POSITION = "STATE_SELECTED_TAB_POSITION";

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

        setupActionBar();
        restoreFragmentMembers();
        restoreCurrentTabSelection(savedInstanceState);

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
                outState.putInt(STATE_SELECTED_TAB_POSITION, tab.getPosition());
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreCurrentTabSelection(savedInstanceState);
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
                SettingsActivity.startActivity(this);
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
            actionBar.addTab(networkTab, 0, false);

            ActionBar.Tab mapTab = actionBar.newTab();
            mapTab.setText(R.string.tab_map);
            mapTab.setTabListener(this);
            actionBar.addTab(mapTab, 1, false);
        }
    }

    private void restoreCurrentTabSelection(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            showNetworkTransactionUsingSharedPreferences();
        } else if (savedInstanceState.containsKey(STATE_SELECTED_TAB_POSITION)) {
            int position = savedInstanceState.getInt(STATE_SELECTED_TAB_POSITION);
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                ActionBar.Tab tab = actionBar.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
            }
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

    private void restoreFragmentMembers() {
        // Fragments can be restored when Activity is re-created by rotation (or other reasons)
        // http://yan-note.blogspot.jp/2012/12/android-actionbarfragmenttabfragmentonc.html
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
        if (fragment == null) {
            return;
        } else if (fragment instanceof EssidListFragment) {
            mEssidListFragment = (EssidListFragment) fragment;
        } else if (fragment instanceof RawNetworkListFragment) {
            mRawNetworkListFragment = (RawNetworkListFragment) fragment;
        } else if (fragment instanceof NetworkMapFragment) {
            mNetworkMapFragment = (NetworkMapFragment) fragment;
        }
    }

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
        if (mRawNetworkListFragment == null) {
            mRawNetworkListFragment = new RawNetworkListFragment();
        }
        if (!mRawNetworkListFragment.isAdded()) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment, mRawNetworkListFragment);
            transaction.commit();
        }
    }

    private void showEssidFragment() {
        if (mEssidListFragment == null) {
            mEssidListFragment = new EssidListFragment();
        }
        if (!mEssidListFragment.isAdded()) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment, mEssidListFragment);
            transaction.commit();
        }
    }

    private void showMapFragment() {
        if (mNetworkMapFragment == null) {
            mNetworkMapFragment = new NetworkMapFragment();
        }
        if (!mNetworkMapFragment.isAdded()) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment, mNetworkMapFragment);
            transaction.commit();
        }
    }
}
