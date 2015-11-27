package com.example.deviceowner;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import com.example.deviceowner.common.Observable;
import com.example.deviceowner.common.ObservableValue;
import com.example.deviceowner.common.ValueObserver;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class AppsManager {
	private static AppsManager sInstance;

	private final Context mContext;
	private final DevicePolicyManager mDevicePolicyManager;
	private final ComponentName mAdminComponent;

	private final ObservableValue<State> mStateObservable = new ObservableValue<>(State.IDLE, true);

	private List<ApplicationInfo> mAppsList = Lists.newArrayList();

	public static void createInstance(final Context context) {
		sInstance = new AppsManager(context);
	}

	public static AppsManager getInstance() {
		return sInstance;
	}

	private AppsManager(final Context context) {
		mContext = context;
		mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminComponent = AdministrationModeManager.getInstance().getAdminComponent();
	}

	public void reloadApps() {
		if (mStateObservable.getValue() != State.IDLE) {
			return;
		}

		mStateObservable.setValue(State.LOADING);

		new LoadingTask().execute();
	}

	public void showApp(final ApplicationInfo app) {
		if (mStateObservable.getValue() != State.IDLE) {
			return;
		}

		mDevicePolicyManager.setApplicationHidden(mAdminComponent, app.packageName, false);
	}

	public void hideApp(final ApplicationInfo app) {
		if (mStateObservable.getValue() != State.IDLE) {
			return;
		}

		mDevicePolicyManager.setApplicationHidden(mAdminComponent, app.packageName, true);
	}

	public boolean isAppHidden(final ApplicationInfo app) {
		return mDevicePolicyManager.isApplicationHidden(mAdminComponent, app.packageName);
	}

	public Observable<ValueObserver<State>> getStateObservable() {
		return mStateObservable;
	}

	public ImmutableList<ApplicationInfo> getAppsList() {
		return ImmutableList.copyOf(mAppsList);
	}

	private class LoadingTask extends AsyncTask<Void, Void, List<ApplicationInfo>> {
		@Override
		protected List<ApplicationInfo> doInBackground(final Void... params) {
			final PackageManager packageManager = mContext.getPackageManager();
			return packageManager.getInstalledApplications(
					PackageManager.GET_META_DATA | PackageManager.GET_UNINSTALLED_PACKAGES
			);
		}

		@Override
		protected void onPostExecute(final List<ApplicationInfo> result) {
			if (result != null) {
				mAppsList = result;
			} else {
				mAppsList = Lists.newArrayList();
			}

			mStateObservable.setValue(State.IDLE);
		}
	}

	public enum State {
		IDLE, LOADING
	}
}
