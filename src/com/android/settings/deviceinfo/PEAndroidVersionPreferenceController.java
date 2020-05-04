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

package com.android.settings.deviceinfo;

import android.content.Context;
import android.os.SystemProperties;
import android.support.v7.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class PEAndroidVersionPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin {

    private static final String PEANDROID_PROPERTY = "ro.peandroid.version";
    private static final String KEY_PEANDROID_PROPERTY = "peandroid_version";

    public PEAndroidVersionPreferenceController(Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return !SystemProperties.get(PEANDROID_PROPERTY).isEmpty();
    }

    @Override
    public String getPreferenceKey() {
        return KEY_PEANDROID_PROPERTY;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setSummary(SystemProperties.get(PEANDROID_PROPERTY,
                mContext.getResources().getString(R.string.device_info_default)));
    }
}
