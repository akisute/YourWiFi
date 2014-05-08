package com.akisute.yourwifi.app;

import com.akisute.android.daggered.DaggeredApplication;
import com.akisute.yourwifi.app.dagger.ApplicationModule;

public class Application extends DaggeredApplication {
    @Override
    protected Object[] getModules() {
        return new Object[]{
                new ApplicationModule()
        };
    }
}
