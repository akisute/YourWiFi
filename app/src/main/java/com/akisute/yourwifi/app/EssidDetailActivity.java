package com.akisute.yourwifi.app;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.akisute.android.daggered.DaggeredActivity;


public class EssidDetailActivity extends DaggeredActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essid_detail);
    }
}
