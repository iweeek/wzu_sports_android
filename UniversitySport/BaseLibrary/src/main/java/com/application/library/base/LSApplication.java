package com.application.library.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.karumi.dexter.Dexter;

public class LSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
