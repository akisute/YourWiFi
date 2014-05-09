package com.akisute.yourwifi.app;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.akisute.android.daggered.DaggeredService;
import com.akisute.yourwifi.app.intent.Intents;
import com.akisute.yourwifi.app.model.NetworkScanManager;

import javax.inject.Inject;

public class NetworkRecordingService extends DaggeredService {

    public static void start(Context context) {
        Intent intent = new Intent(context, NetworkRecordingService.class);
        intent.setAction(Intents.ACTION_RECORD);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, NetworkRecordingService.class);
        context.stopService(intent);
    }

    public class LocalBinder extends Binder {
        NetworkRecordingService getService() {
            return NetworkRecordingService.this;
        }
    }

    private final LocalBinder mBinder = new LocalBinder();

    @Inject
    NetworkScanManager mNetworkScanManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handleStop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Intents.ACTION_RECORD.equals(action)) {
                handleStart();
                return START_STICKY;
            }
        }
        // Unexpected intents, do nothing and stop immediately
        stopSelfResult(startId);
        return START_NOT_STICKY;
    }

    private void handleStart() {
        mNetworkScanManager.startScan();
    }

    private void handleStop() {
        mNetworkScanManager.stopScan();
    }
}
