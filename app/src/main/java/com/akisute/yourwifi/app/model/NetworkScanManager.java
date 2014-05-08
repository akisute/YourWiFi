package com.akisute.yourwifi.app.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.akisute.android.daggered.ForApplication;
import com.akisute.yourwifi.app.util.GlobalEventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * NOTE:
 * Some very useful articles about WifiManager.startScan() behavior are available in here:
 * http://stackoverflow.com/questions/14862018/startscan-has-result-after-10-min-when-phone-get-into-idle-state
 * - After calling startScan(), WifiManager automatically scans for Wifi Networks in periodic times. This period is defined in Android System on build time, that means it's device dependent.
 * - Looks like scanning keeps running until Wifi is turned off or enters sleeping mode. There's no explicit method to immediately stop scans.
 */
public class NetworkScanManager {

    @Inject
    Context mContext;
    @Inject
    WifiManager mWifiManager;
    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    NetworkCache mNetworkCache;

    private boolean mScanning;
    private WifiManager.WifiLock mWifiLock;
    private ScanResultsAvailableReceiver mScanResultsAvailableReceiver;

    @Inject
    public NetworkScanManager(@ForApplication Context context, WifiManager wifiManager, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        mContext = context;
        mWifiManager = wifiManager;
        mGlobalEventBus = globalEventBus;
        mNetworkCache = networkCache;
    }

    public boolean startScan() {
        if (mWifiLock == null) {
            mWifiLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, NetworkScanManager.class.getSimpleName());
        }
        if (!mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }

        boolean started = mWifiManager.startScan();
        mScanning |= started; // if mScanning is already true, assume WifiManager is already scanning anyway. The only case mScanning == false is when not already started and the latest startScan failed as well.

        if (mScanning) {
            if (mScanResultsAvailableReceiver == null) {
                mScanResultsAvailableReceiver = new ScanResultsAvailableReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                mContext.registerReceiver(mScanResultsAvailableReceiver, intentFilter);
            }
            Log.d(NetworkScanManager.class.getSimpleName(), String.format("Started scanning."));
        }

        return mScanning;
    }

    public void stopScan() {
        mScanning = false;
        if (mWifiLock != null) {
            mWifiLock.release();
            mWifiLock = null;
        }
        if (mScanResultsAvailableReceiver != null) {
            mContext.unregisterReceiver(mScanResultsAvailableReceiver);
            mScanResultsAvailableReceiver = null;
        }
        Log.d(NetworkScanManager.class.getSimpleName(), String.format("Stopped scanning."));
    }

    public boolean isScanning() {
        return mScanning;
    }

    private class ScanResultsAvailableReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = mWifiManager.getScanResults();
            if (scanResultList == null) {
                return;
            }

            List<Network> networkList = new ArrayList<Network>(scanResultList.size());
            for (ScanResult scanResult : scanResultList) {
                Network network = Network.newInstance(scanResult);
                if (network == null) {
                    continue;
                }
                mNetworkCache.put(network);
                networkList.add(network);
            }
            mGlobalEventBus.postInMainThread(new OnNewScanResultsEvent(networkList));
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
