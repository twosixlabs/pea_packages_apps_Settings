/*
 * This work was authored by Two Six Labs, LLC and is sponsored by a subcontract agreement with
 * Raytheon BBN Technologies Corp. under Prime Contract No. FA8750-16-C-0006 with the Air Force
 * Research Laboratory (AFRL).
 *
 * The Government has unlimited rights to use, modify, reproduce, release, perform, display, or disclose
 * computer software or computer software documentation marked with this legend. Any reproduction of
 * technical data, computer software, or portions thereof marked with this legend must also reproduce
 * this marking.
 *
 * Copyright (C) 2020 Two Six Labs, LLC.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.privacy;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.privacy.IPrivacyManager;
import com.android.settings.R;

import java.util.ArrayList;
import java.util.List;

public class PrivacyManagerSettings extends ListFragment {
	private ComponentName currentManager;
	private List<ComponentName> privacyManagers = new ArrayList<>();
	private IPrivacyManager mService;
	private int currentSelection = -1;

	public PrivacyManagerSettings() {}

	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
						updateList();
				}
		};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mService = IPrivacyManager.Stub.asInterface(ServiceManager.getService("privacy_manager"));
        getActivity().setTitle(R.string.privacy_managers_title);
	}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {
						return inflater.inflate(R.layout.privacy_manager_settings, container, false);
		}

		@Override
		public void onViewCreated (View view, Bundle savedInstanceState) {
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								currentSelection = position;
						}
				});

				view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    		if (currentSelection > -1) {
                    			try {
                       			mService.setCurrentManager((ComponentName) getListView().getItemAtPosition(currentSelection));
                       			Toast.makeText(PrivacyManagerSettings.this.getActivity(), "set manager", Toast.LENGTH_SHORT).show();
                       		} catch (RemoteException e) {
                       			e.printStackTrace();
                       			Toast.makeText(PrivacyManagerSettings.this.getActivity(), "Could not save settings", Toast.LENGTH_SHORT).show();
                       		}
                      	}
                    }
                });
		}

		@Override
		public void onResume() {
			super.onResume();
			updateList();

			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_PACKAGE_ADDED);
			filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
			filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			filter.addDataScheme("package");
			getActivity().registerReceiver(mBroadcastReceiver, filter);
		}

		@Override
		public void onPause() {
			super.onPause();

			getActivity().unregisterReceiver(mBroadcastReceiver);
		}

		private void updateList() {
			if (mService != null) {
				try {
					privacyManagers = mService.getAvailableManagers();
					currentManager = mService.getCurrentManagerName();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			getListView().setAdapter(new PrivacyManagerListAdapter());
		}


	class PrivacyManagerListAdapter extends BaseAdapter {
				final LayoutInflater mInflater;

				PrivacyManagerListAdapter() {
						mInflater = (LayoutInflater)
										getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				}

				@Override
				public boolean hasStableIds() {
						return false;
				}

				@Override
				public int getCount() {
						return privacyManagers.size();
				}

				@Override
				public Object getItem(int position) {
						if (position < 0) {
								throw new ArrayIndexOutOfBoundsException();
						}
						return privacyManagers.get(position);
				}

				@Override
				public long getItemId(int position) {
						return position;
				}

				@Override
				public boolean areAllItemsEnabled() {
						return false;
				}

				@Override
				public int getViewTypeCount() {
						return 1;
				}

				@Override
				public int getItemViewType(int position) {
						return 1;
				}

				@Override
				public boolean isEnabled(int position) {
						return true;
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
						ComponentName item = (ComponentName) getItem(position);
						if (convertView == null) {
							convertView = mInflater.inflate(R.layout.privacy_manager_item, parent, false);
						}

                        String packageName = item.getPackageName();
                        String displayName = packageName;
                        try {
                            PackageManager pm = getContext().getPackageManager();
                            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                            String label = pm.getApplicationLabel(ai).toString();
                            if (label != null) {
                                displayName = label;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                        }

						((TextView) convertView.findViewById(R.id.name)).setText(displayName);

						if (item.equals(currentManager)) {
							PrivacyManagerSettings.this.getListView().setItemChecked(position, true);
						}
						return convertView;
				}
		}
}
