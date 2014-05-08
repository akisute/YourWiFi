package com.akisute.yourwifi.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.model.NetworkScanManager;

import javax.inject.Inject;


public class MainActivity extends DaggeredActivity {

    @Inject
    NetworkScanManager mNetworkScanManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNetworkScanManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNetworkScanManager.stopScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
