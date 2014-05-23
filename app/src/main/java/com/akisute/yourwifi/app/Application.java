package com.akisute.yourwifi.app;

import com.akisute.android.daggered.DaggeredApplication;
import com.akisute.yourwifi.app.dagger.ApplicationModule;
import com.parse.Parse;

public class Application extends DaggeredApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "QDqla70qFIIfsls2HQRo1qMZM2wCGTkwjlZImaYb", "u64kUPWaPcvikkVzVWwWag6uvpgAPONF3HI8RHjk");
    }

    @Override
    protected Object[] getModules() {
        return new Object[]{
                new ApplicationModule()
        };
    }
}
