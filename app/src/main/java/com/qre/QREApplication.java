package com.qre;

import android.app.Application;

import com.qre.injection.Injector;

public class QREApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Injector.initializeApplicationComponent(this);
	}

}