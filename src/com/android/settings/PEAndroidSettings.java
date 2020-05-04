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

package com.android.settings;

import android.content.Context;
import android.provider.SearchIndexableResource;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settings.privacy.PrivacyManagerSettings;
import com.android.settings.privacy.ActivePolicyManagerPreferenceController;

import java.util.ArrayList;
import java.util.List;

public class PEAndroidSettings extends DashboardFragment {
    private static final String TAG = "PEAndroidSettings";

    @Override
    public int getMetricsCategory() {
	// Unsure what this should be, look in metrics_constants.proto
        return MetricsEvent.VIEW_UNKNOWN;
    }

    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.peandroid_settings;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
	final List<AbstractPreferenceController> controllers = new ArrayList<>();
	controllers.add(new ActivePolicyManagerPreferenceController(context));
	return controllers;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
	new BaseSearchIndexProvider() {
	    @Override
	    public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean enabled) {
		final ArrayList<SearchIndexableResource> result = new ArrayList<>();
		final SearchIndexableResource sir = new SearchIndexableResource(context);
		sir.xmlResId = R.xml.peandroid_settings;
		result.add(sir);
		return result;
	    }

	    @Override
	    public List<AbstractPreferenceController> getPreferenceControllers(Context context) {
		return buildPreferenceControllers(context);
	    }
	};
}
