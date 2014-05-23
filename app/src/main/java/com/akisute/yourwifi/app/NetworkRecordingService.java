package com.akisute.yourwifi.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.akisute.android.daggered.DaggeredService;
import com.akisute.yourwifi.app.intent.Intents;
import com.akisute.yourwifi.app.model.LocationScanManager;
import com.akisute.yourwifi.app.model.NetworkCache;
import com.akisute.yourwifi.app.model.NetworkScanManager;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.akisute.yourwifi.app.util.GlobalResources;
import com.akisute.yourwifi.app.util.GlobalSharedPreferences;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class NetworkRecordingService extends DaggeredService {

    private static final int NOTIFICATION_ID = 1;

    public static void start(Context context) {
        Intent intent = new Intent(context, NetworkRecordingService.class);
        intent.setAction(Intents.ACTION_RECORD);
        context.startService(intent);
    }

    public static void stopImmediately(Context context) {
        Intent intent = new Intent(context, NetworkRecordingService.class);
        context.stopService(intent);
    }

    public static PendingIntent getStopPendingIntent(Context context) {
        Intent intent = new Intent(context, NetworkRecordingService.class);
        intent.setAction(Intents.ACTION_STOP);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    public class LocalBinder extends Binder {
        NetworkRecordingService getService() {
            return NetworkRecordingService.this;
        }
    }

    private final LocalBinder mBinder = new LocalBinder();

    @Inject
    GlobalResources mGlobalResources;
    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    GlobalSharedPreferences mGlobalSharedPreferences;
    @Inject
    NotificationManager mNotificationManager;
    @Inject
    NetworkScanManager mNetworkScanManager;
    @Inject
    NetworkCache mNetworkCache;
    @Inject
    LocationScanManager mLocationScanManager;

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
        setupNotification();
        mNetworkScanManager.startScan();
        mLocationScanManager.startScan();
        mGlobalEventBus.register(this);
        Log.d(NetworkRecordingService.class.getSimpleName(), String.format("Service Started."));
    }

    private void handleStop() {
        removeNotification();
        if (mNetworkScanManager.isScanning() || mLocationScanManager.isScanning()) {
            mNetworkScanManager.stopScan();
            mLocationScanManager.stopScan();
            mGlobalEventBus.unregister(this);
        }
        Log.d(NetworkRecordingService.class.getSimpleName(), String.format("Service Stopped."));
    }

    @Subscribe
    public void onNewScanResultsEvent(NetworkScanManager.OnNewScanResultsEvent event) {
        updateNotification();
    }

    @Subscribe
    public void onNetworkListDisplayModeChangedEvent(GlobalSharedPreferences.NetworkListDisplayMode.OnChangeEvent event) {
        updateNotification();
    }

    private Notification createNotification() {
        int count;
        switch (mGlobalSharedPreferences.getNetworkListDisplayMode()) {
            case GlobalSharedPreferences.NetworkListDisplayMode.SHOW_ESSIDS:
                count = mNetworkCache.getEssidCount();
                break;
            case GlobalSharedPreferences.NetworkListDisplayMode.SHOW_RAW_NETWORKS:
                count = mNetworkCache.getNetworkCount();
                break;
            default:
                count = mNetworkCache.getEssidCount();
                break;
        }

        String appName = mGlobalResources.getResources().getString(R.string.app_name);
        String contentTitle = mGlobalResources.getResources().getQuantityString(R.plurals.activity_main_title, count, appName, count);
        String contentText = mGlobalResources.getResources().getString(R.string.notification_stop_service);
        return new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(getStopPendingIntent(this))
                .setDeleteIntent(getStopPendingIntent(this))
                .setShowWhen(false)
                .setAutoCancel(false)   // Notification.FLAG_AUTO_CANCEL
                .setOngoing(true)       // Notification.FLAG_ONGOING_EVENT and Notification.FLAG_NO_CLEAR (http://stackoverflow.com/questions/5338501/android-keep-notification-steady-at-notification-bar)
                .build();
    }

    private void setupNotification() {
        startForeground(NOTIFICATION_ID, createNotification());
    }

    private void updateNotification() {
        mNotificationManager.notify(NOTIFICATION_ID, createNotification());
    }

    private void removeNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
