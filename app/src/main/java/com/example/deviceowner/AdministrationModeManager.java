package com.example.deviceowner;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class AdministrationModeManager {
	private static AdministrationModeManager sInstance;

	private final Context mContext;
	private final ComponentName mAdminComponent;
	private final DevicePolicyManager mDevicePolicyManager;

	public static void createInstance(final Context context) {
		sInstance = new AdministrationModeManager(context);
	}

	public static AdministrationModeManager getInstance() {
		return sInstance;
	}

	public AdministrationModeManager(final Context context) {
		mContext = context;
		mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminComponent = new ComponentName(mContext, AdminReceiver.class);
	}

	public ComponentName getAdminComponent() {
		return mAdminComponent;
	}

	public boolean isAdministrator() {
		return mDevicePolicyManager.isAdminActive(mAdminComponent);
	}

	public boolean isDeviceOwner() {
		return mDevicePolicyManager.isDeviceOwnerApp(mContext.getPackageName());
	}
}
