package com.akisute.yourwifi.app;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.cloud.CloudManager;

import javax.inject.Inject;

public class SettingsActivity extends DaggeredActivity {

    @Inject
    CloudManager mCloudManager;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.activity_settings_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem loginItem = menu.findItem(R.id.action_login);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);

        boolean isLoggedIn = mCloudManager.isLoggedIn();
        loginItem.setVisible(!isLoggedIn);
        logoutItem.setVisible(isLoggedIn);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                CloudLoginDialogFragment dialogFragment = new CloudLoginDialogFragment();
                FragmentManager fragmentManager = getFragmentManager();
                dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
                return true;
            case R.id.action_logout:
                // TODO: logout via API
                return true;
        }
        return false;
    }
}
