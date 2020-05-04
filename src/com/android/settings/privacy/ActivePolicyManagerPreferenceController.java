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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v7.preference.Preference;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.internal.privacy.IPrivacyManager;

public class ActivePolicyManagerPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin {

    private static final String TAG = "ActivePolicyManagerPrefCtrl";
    private static final String KEY_ACTIVE_POLICY_MANAGER = "active_policy_manager";

    private Preference mPreference;
    private IPrivacyManager mService;

    public ActivePolicyManagerPreferenceController(Context context) {
	super(context);
	mService = IPrivacyManager.Stub.asInterface(ServiceManager.getService("privacy_manager"));
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_ACTIVE_POLICY_MANAGER;
    }

    @Override
    public void updateState(Preference preference) {
        if (preference != null) {
            try {
                ComponentName currentManager = mService.getCurrentManagerName();
                if (currentManager != null) {
                    String packageName = mService.getCurrentManagerName().getPackageName();
                    String displayName = packageName;

                    try {
                        PackageManager pm = mContext.getPackageManager();
                        ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                        String label = pm.getApplicationLabel(ai).toString();
                        if (label != null) {
                            displayName = label;
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                    preference.setSummary(displayName);

                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
                    preference.setIntent(intent);
                } else {
                    preference.setSummary(mContext.getString(R.string.no_privacy_managers));
                    preference.setIntent(null);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
