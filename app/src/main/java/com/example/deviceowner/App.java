package com.example.deviceowner;

import android.app.Application;

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		AdministrationModeManager.createInstance(this);
		AppsManager.createInstance(this);
		AppsManager.getInstance().reloadApps();
	}
}
