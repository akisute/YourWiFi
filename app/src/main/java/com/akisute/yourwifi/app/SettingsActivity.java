package com.akisute.yourwifi.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.akisute.android.daggered.DaggeredActivity;

public class SettingsActivity extends DaggeredActivity {

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
}
