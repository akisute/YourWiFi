package com.akisute.yourwifi.app.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.akisute.yourwifi.app.util.GlobalEventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * NOTE:
 * Some very useful articles about WifiManager.startScan() behavior are available in here:
 * http://stackoverflow.com/questions/14862018/startscan-has-result-after-10-min-when-phone-get-into-idle-state
 * - After calling startScan(), WifiManager automatically scans for Wifi Networks in periodic times. This period is defined in Android System on build time, that means it's device dependent.
 * - Looks like scanning keeps running until Wifi is turned off or enters sleeping mode. There's no explicit method to immediately stop scans.
 */
public class NetworkScanManager {

    private final ScanResultsAvailableReceiver mScanResultsAvailableReceiver;
    private Context mContext;
    private boolean mScanning;
    private WifiManager.WifiLock mWifiLock;

    private static NetworkScanManager INSTANCE = new NetworkScanManager();

    public static NetworkScanManager getInstance() {
        return INSTANCE;
    }

    private NetworkScanManager() {
        mScanResultsAvailableReceiver = new ScanResultsAvailableReceiver();
    }

    public void registerInContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null. Use unregister() if you meant to unregister.");
        }
        if (mContext != null) {
            if (mContext.equals(context)) {
                return;
            } else {
                throw new IllegalStateException(String.format("Already registered in another context %s", mContext));
            }
        }
        mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(mScanResultsAvailableReceiver, intentFilter);
        Log.d(NetworkScanManager.class.getSimpleName(), String.format("Registered in context %s.", context));
    }

    public void unregister() {
        mScanning = false;
        if (mWifiLock != null) {
            mWifiLock.release();
            mWifiLock = null;
        }
        if (mContext != null) {
            mContext.unregisterReceiver(mScanResultsAvailableReceiver);
            mContext = null;
        }
        Log.d(NetworkScanManager.class.getSimpleName(), "Unregistered.");
    }

    public boolean startScan() {
        validateContext();

        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (mWifiLock == null) {
            mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, NetworkScanManager.class.getSimpleName());
        }
        if (!mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }

        boolean started = wifiManager.startScan();
        mScanning |= started; // if mScanning is already true, assume WifiManager is already scanning anyway. The only case mScanning == false is when not already started and the latest startScan failed as well.
        Log.d(NetworkScanManager.class.getSimpleName(), String.format("Started scanning: %b", mScanning));
        return mScanning;
    }

    public boolean isScanning() {
        return mScanning;
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

            NetworkCache networkCache = NetworkCache.getInstance();
            List<Network> networkList = new ArrayList<Network>(scanResultList.size());
            for (ScanResult scanResult : scanResultList) {
                Network network = Network.newInstance(scanResult);
                if (network == null) {
                    continue;
                }
                networkCache.put(network);
                networkList.add(network);
            }
            GlobalEventBus.getInstance().postInMainThread(new OnNewScanResultsEvent(networkList));
        }
    }

    public static class OnNewScanResultsEvent {

        private List<Network> mNetworkList;

        public OnNewScanResultsEvent(List<Network> networkList) {
            mNetworkList = networkList;
        }

        public List<Network> getNetworkList() {
            return mNetworkList;
        }
    }
}
