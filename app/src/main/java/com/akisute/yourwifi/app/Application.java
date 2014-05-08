package com.akisute.yourwifi.app;

import com.akisute.android.daggered.DaggeredApplication;

public class Application extends DaggeredApplication {
    @Override
    protected Object[] getModules() {
        return new Object[0];
    }
}
