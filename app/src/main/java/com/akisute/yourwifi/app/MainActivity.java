package com.akisute.yourwifi.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;


public class MainActivity extends DaggeredActivity {

    @Inject
    GlobalEventBus mGlobalEventBus;

    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showEssidFragment();
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

        MenuItem menuActionShowBssids = menu.findItem(R.id.action_show_bssids);
        MenuItem menuActionShowEssids = menu.findItem(R.id.action_show_essids);

        if (mCurrentFragment instanceof RawNetworkListFragment) {
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
    }

    private void showEssidFragment() {
        EssidListFragment fragment = new EssidListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        mCurrentFragment = fragment;
    }
}
