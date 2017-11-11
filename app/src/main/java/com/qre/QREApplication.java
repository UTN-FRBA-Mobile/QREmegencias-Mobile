package com.qre;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.qre.injection.Injector;

public class QREApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        Injector.initializeApplicationComponent(this);
    }

}