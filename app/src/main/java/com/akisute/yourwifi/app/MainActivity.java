package com.akisute.yourwifi.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.model.NetworkScanManager;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;


public class MainActivity extends DaggeredActivity {

    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    NetworkScanManager mNetworkScanManager;

    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showEssidFragment();
        mGlobalEventBus.register(this);
        mNetworkScanManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGlobalEventBus.unregister(this);
        mNetworkScanManager.stopScan();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuActionShowBssids = menu.findItem(R.id.action_show_bssids);
        MenuItem menuActionShowEssids = menu.findItem(R.id.action_show_essids);

        if (mCurrentFragment instanceof NetworkListFragment) {
            menuActionShowBssids.setVisible(false);
            menuActionShowEssids.setVisible(true);
        } else if (mCurrentFragment instanceof EssidListFragment) {
            menuActionShowBssids.setVisible(true);
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
            case R.id.action_show_bssids:
                showBssidFragment();
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
        Intent intent = new Intent(this, EssidDetailActivity.class);
        startActivity(intent);
    }

    private void showBssidFragment() {
        NetworkListFragment fragment = new NetworkListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        mCurrentFragment = fragment;
    }

    private void showEssidFragment() {
        EssidListFragment fragment = new EssidListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        mCurrentFragment = fragment;
    }
}
