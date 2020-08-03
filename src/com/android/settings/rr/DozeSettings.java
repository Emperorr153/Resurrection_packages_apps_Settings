/*Copyright (C) 2015 The ResurrectionRemix Project
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
          http://www.apache.org/licenses/LICENSE-2.0
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/
package com.android.settings.rr;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.UserHandle;
import android.provider.Settings;
import android.net.Uri;

import com.android.settings.Utils;
import com.android.settings.rr.Preferences.CustomSeekBarPreference;

import com.android.settings.rr.Preferences.SystemSettingColorPickerPreference;

import android.provider.SearchIndexableResource;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.rr.utils.RRUtils;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;

import com.android.settings.rr.Preferences.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
@SearchIndexable
public class DozeSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
        protected static final String DOZE_BRIGHTNESS_KEY = "ambient_doze_brightness";
        protected static final String PULSE_BRIGHTNESS_KEY = "ambient_pulse_brightness";

        private SystemSettingSeekBarPreference mDozeBrightness;
        private SystemSettingSeekBarPreference mPulseBrightness;

        private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rr_doze);
        mContext = getActivity();

            int defaultDoze = getResources().getInteger(
                    com.android.internal.R.integer.config_screenBrightnessDoze);
            int defaultPulse = getResources().getInteger(
                    com.android.internal.R.integer.config_screenBrightnessPulse);
            if (defaultPulse == -1) {
                defaultPulse = defaultDoze;
            }

            mPulseBrightness = (SystemSettingSeekBarPreference) findPreference(PULSE_BRIGHTNESS_KEY);
            int value = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.PULSE_BRIGHTNESS, defaultPulse);
            mPulseBrightness.setValue(value);
            mPulseBrightness.setOnPreferenceChangeListener(this);

            mDozeBrightness = (SystemSettingSeekBarPreference) findPreference(DOZE_BRIGHTNESS_KEY);
            value = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.DOZE_BRIGHTNESS, defaultDoze);
            mDozeBrightness.setValue(value);
            mDozeBrightness.setOnPreferenceChangeListener(this);

        int anim = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RR_CONFIG_ANIM, 0);
        try {
            if (anim == 0) {
                removePreference("animation");
            } else if (anim == 1) {
                removePreference("preview");
            } else if (anim == 2) {
                removePreference("animation");
                removePreference("preview");
            }
        } catch (Exception e) {}
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
             if (preference == mPulseBrightness) {
                int value = (Integer) newValue;
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.PULSE_BRIGHTNESS, value);
                return true;
            } else if (preference == mDozeBrightness) {
                int value = (Integer) newValue;
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.DOZE_BRIGHTNESS, value);
                return true;
            }
       return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.RESURRECTED;
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {
            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean enabled) {
                ArrayList<SearchIndexableResource> result =
                    new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.rr_doze;
                    result.add(sir);
                    return result;
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                List<String> keys = super.getNonIndexableKeys(context);
                return keys;
            }
        };
}
