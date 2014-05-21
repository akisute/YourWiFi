package com.akisute.yourwifi.app.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class GlobalEventBus extends Bus {

    private final Handler mMainThreadHandler;

    public GlobalEventBus() {
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void postInMainThread(final Object event) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                post(event);
            }
        });
    }
}
