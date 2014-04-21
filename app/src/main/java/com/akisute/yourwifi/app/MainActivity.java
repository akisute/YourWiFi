package com.akisute.yourwifi.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.yourwifi.app.model.NetworkScanManager;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkScanManager networkScanManager = NetworkScanManager.getInstance();
        networkScanManager.registerInContext(getApplicationContext());
        networkScanManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkScanManager networkScanManager = NetworkScanManager.getInstance();
        networkScanManager.unregister();
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
