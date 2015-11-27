package com.example.deviceowner;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import com.example.deviceowner.common.ValueObserver;

import java.util.List;

public class MainActivity extends AppCompatActivity {

	private final AppsManager mAppsManager = AppsManager.getInstance();
	private final AppsManagerStateObserver mAppsManagerStateObserver = new AppsManagerStateObserver();

	private AppsListAdapter mAppsListAdapter;

	private RecyclerView mAppsRecyclerView;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAppsRecyclerView = (RecyclerView) findViewById(R.id.view_apps_list);
		mProgressBar = (ProgressBar) findViewById(R.id.view_loading_apps_progressbar);

		mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAppsListAdapter = new AppsListAdapter(this);
		mAppsRecyclerView.setAdapter(mAppsListAdapter);

		mAppsManager.getStateObservable().registerObserver(mAppsManagerStateObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mAppsManager.getStateObservable().unregisterObserver(mAppsManagerStateObserver);
		mAppsRecyclerView = null;
		mProgressBar = null;
	}

	private static class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.ViewHolder> {
		private final Context mContext;
		private final LayoutInflater mInflater;
		private final PackageManager mPackageManager;
		private List<ApplicationInfo> mAppsList;
		private final AdministrationModeManager mAdministrationModeManager = AdministrationModeManager.getInstance();
		private final AppsManager mAppsManager = AppsManager.getInstance();

		public AppsListAdapter(final Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mPackageManager = mContext.getPackageManager();
		}

		@Override
		public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			final View layout = mInflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
			return new ViewHolder(layout);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, final int position) {
			holder.mAppTitleTextView.setText(mAppsList.get(position).loadLabel(mPackageManager));
			if (mAdministrationModeManager.isAdministrator() && mAdministrationModeManager.isDeviceOwner()) {
				holder.mAppTitleTextView.setChecked(!mAppsManager.isAppHidden(mAppsList.get(position)));
			}
		}

		@Override
		public int getItemCount() {
			return mAppsList == null ? 0 : mAppsList.size();
		}

		public void setAppsList(final List<ApplicationInfo> appsList) {
			mAppsList = appsList;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			public final CheckedTextView mAppTitleTextView;

			public ViewHolder(final View itemView) {
				super(itemView);

				mAppTitleTextView = (CheckedTextView) itemView.findViewById(android.R.id.text1);
				mAppTitleTextView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(final View v) {
						if (mAdministrationModeManager.isAdministrator() &&
								mAdministrationModeManager.isDeviceOwner()) {
							mAppTitleTextView.setChecked(!mAppTitleTextView.isChecked());
							if (mAppTitleTextView.isChecked()) {
								mAppsManager.hideApp(mAppsList.get(getAdapterPosition()));
							} else {
								mAppsManager.showApp(mAppsList.get(getAdapterPosition()));
							}
						}
					}
				});
			}
		}
	}

	private class AppsManagerStateObserver implements ValueObserver<AppsManager.State> {
		@Override
		public void onChanged(final AppsManager.State value) {
			switch (value) {
				case IDLE:
					mAppsRecyclerView.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);

					mAppsListAdapter.setAppsList(mAppsManager.getAppsList());
					mAppsListAdapter.notifyDataSetChanged();
					break;

				case LOADING:
					mAppsRecyclerView.setVisibility(View.GONE);
					mProgressBar.setVisibility(View.VISIBLE);
					break;
			}
		}
	}
}
