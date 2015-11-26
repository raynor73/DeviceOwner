package com.example.deviceowner;

import android.app.Application;

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		AppsManager.createInstance(this);
		AppsManager.getInstance().reloadApps();
	}
}
