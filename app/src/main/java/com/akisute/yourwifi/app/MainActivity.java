package com.akisute.yourwifi.app;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragmentUsingGlobalSharedPreferences();
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

    @Subscribe
    public void onEssidListItemSelectedEvent(EssidListFragment.OnEssidListItemSelectedEvent event) {
        EssidDetailActivity.startActivity(this, event.getEssid());
    }

    @Subscribe
    public void onNetworkListDisplayModeChangeEvent(GlobalSharedPreferences.NetworkListDisplayMode.OnChangeEvent event) {
        showFragmentUsingGlobalSharedPreferences();
    }

    private void showFragmentUsingGlobalSharedPreferences() {
        // TODO: do not switch fragment, if tabs other than network list is selected. ...possibly ok since container should not be shared between network lists and maps
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
        // TODO: These fragments should be reused using getFragmentManager().putFragment()
        RawNetworkListFragment fragment = new RawNetworkListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    private void showEssidFragment() {
        // TODO: These fragments should be reused using getFragmentManager().putFragment()
        EssidListFragment fragment = new EssidListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }
}
