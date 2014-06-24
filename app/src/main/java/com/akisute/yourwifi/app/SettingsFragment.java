package com.akisute.yourwifi.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;

import com.akisute.android.daggered.DaggeredPreferenceFragment;
import com.akisute.yourwifi.app.cloud.CloudManager;
import com.akisute.yourwifi.app.util.GlobalResources;

import javax.inject.Inject;

public class SettingsFragment extends DaggeredPreferenceFragment {

    @Inject
    GlobalResources mGlobalResources;
    @Inject
    CloudManager mCloudManager;

    private Preference mLoginStatusPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mLoginStatusPreference = findPreference(mGlobalResources.getResources().getString(R.string.settings_login_status_key));
        mLoginStatusPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CloudLoginDialogFragment dialogFragment = new CloudLoginDialogFragment();
                FragmentManager fragmentManager = getFragmentManager();
                dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
                return true;
            }
        });

        if (mCloudManager.isLoggedIn()) {
            mLoginStatusPreference.setTitle(mGlobalResources.getResources().getString(R.string.settings_login_status_title_login, "admin"));
            mLoginStatusPreference.setSummary(R.string.settings_login_status_summary_login);
            mLoginStatusPreference.setSelectable(false);
        } else {
            mLoginStatusPreference.setTitle(R.string.settings_login_status_title_notloggedin);
            mLoginStatusPreference.setSummary(R.string.settings_login_status_summary_notloggedin);
            mLoginStatusPreference.setSelectable(true);
        }
    }
}
