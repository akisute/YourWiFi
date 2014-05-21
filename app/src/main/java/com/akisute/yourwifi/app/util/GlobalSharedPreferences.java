package com.akisute.yourwifi.app.util;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class GlobalSharedPreferences {

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    public GlobalSharedPreferences(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public static final class NetworkListDisplayMode {
        static final String KEY = "NetworkListDisplayMode";
        public static final int SHOW_ESSIDS = 0;
        public static final int SHOW_RAW_NETWORKS = 1;
    }

    public int getNetworkListDisplayMode() {
        return mSharedPreferences.getInt(NetworkListDisplayMode.KEY, NetworkListDisplayMode.SHOW_ESSIDS);
    }

    public void setNetworkListDisplayMode(int mode) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(NetworkListDisplayMode.KEY, mode);
        editor.commit();
    }
}
