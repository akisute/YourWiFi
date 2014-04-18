package com.akisute.yourwifi.app.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiNetworkManager {

    private Context mContext;
    private ScanResultsAvailableReceiver mScanResultsAvailableReceiver;

    private static WifiNetworkManager INSTANCE = new WifiNetworkManager();
    public static WifiNetworkManager getInstance() {
        return INSTANCE;
    }

    private WifiNetworkManager() {
        mScanResultsAvailableReceiver = new ScanResultsAvailableReceiver();
    }

    public void registerInContext(Context context) {
        if (mContext != null) {
            if (mContext.equals(context)) {
                return;
            } else {
                throw new IllegalStateException(String.format("Already registered in another context %s", mContext.toString()));
            }
        }
        mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(mScanResultsAvailableReceiver, intentFilter);
    }

    public void unregister() {
        mContext.unregisterReceiver(mScanResultsAvailableReceiver);
        mContext = null;
    }

    public void startScan() {
        validateContext();
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
    }

    private void validateContext() {
        if (mContext == null) {
            throw new IllegalStateException("Must be registered in context before using this class.");
        }
    }

    private class ScanResultsAvailableReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            List<ScanResult> scanResultList = wifiManager.getScanResults();
            if (scanResultList == null) {
                return;
            }
            for (ScanResult scanResult : scanResultList) {
                Log.d(WifiNetworkManager.class.getSimpleName(), scanResult.toString());
            }
        }
    }
}
