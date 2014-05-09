package com.akisute.yourwifi.app;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.akisute.android.daggered.DaggeredActivity;
import com.akisute.yourwifi.app.model.Essid;
import com.akisute.yourwifi.app.model.NetworkCache;

import javax.inject.Inject;


public class EssidDetailActivity extends DaggeredActivity {

    public static void startActivity(Context context, Essid essid) {
        startActivity(context, essid.getSsid());
    }

    public static void startActivity(Context context, String ssid) {
        Intent intent = new Intent(context, EssidDetailActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("ssid", ssid);
        context.startActivity(intent);
    }

    @Inject
    NetworkCache mNetworkCache;

    private Essid mEssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essid_detail);

        Intent intent = getIntent();
        String ssid = intent.getStringExtra("ssid");
        mEssid = mNetworkCache.getEssid(ssid);

        showFragment();
    }

    private void showFragment() {
        EssidDetailFragment fragment = new EssidDetailFragment();
        fragment.setEssid(mEssid);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }
}
