package com.akisute.yourwifi.app;

import android.os.Bundle;
import android.preference.Preference;

import com.akisute.android.daggered.DaggeredPreferenceFragment;
import com.akisute.yourwifi.app.util.GlobalResources;

import javax.inject.Inject;

public class SettingsFragment extends DaggeredPreferenceFragment {

    @Inject
    GlobalResources mGlobalResources;

    private Preference mLoginStatusPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mLoginStatusPreference = findPreference(mGlobalResources.getResources().getString(R.string.settings_login_status_key));

        // TODO: login state check
        mLoginStatusPreference.setTitle(R.string.settings_login_status_title_notloggedin);
        mLoginStatusPreference.setSummary(R.string.settings_login_status_summary_notloggedin);
        mLoginStatusPreference.setSelectable(true);

//        mLoginStatusPreference.setTitle(mGlobalResources.getResources().getString(R.string.settings_login_status_title_login, "admin"));
//        mLoginStatusPreference.setSummary(R.string.settings_login_status_summary_login);
//        mLoginStatusPreference.setSelectable(false);
    }
}
