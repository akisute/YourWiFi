package com.akisute.yourwifi.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.yourwifi.app.model.NetworkManager;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.registerInContext(getApplicationContext());
        networkManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.unregister();
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
