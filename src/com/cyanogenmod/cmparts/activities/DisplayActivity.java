
package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class DisplayActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    /* Preference Screens */
    private static final String BACKLIGHT_SETTINGS = "backlight_settings";

    private static final String GENERAL_CATEGORY = "general_category";

    private PreferenceScreen mBacklightScreen;

    /* Other */
    private static final String ROTATE_180_PREF = "pref_rotate_180";

    private CheckBoxPreference mRotate180Pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.display_settings_title_subhead);
        addPreferencesFromResource(R.xml.display_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Preference Screens */
        mBacklightScreen = (PreferenceScreen) prefSet.findPreference(BACKLIGHT_SETTINGS);
        // No reason to show backlight if no light sensor on device
        if (((SensorManager) getSystemService(SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_LIGHT) == null) {
            ((PreferenceCategory) prefSet.findPreference(GENERAL_CATEGORY))
                    .removePreference(mBacklightScreen);
        }

        /* Rotate 180 */
        mRotate180Pref = (CheckBoxPreference) prefSet.findPreference(ROTATE_180_PREF);
        mRotate180Pref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION_MODE, 0) == 2);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mBacklightScreen) {
            startActivity(mBacklightScreen.getIntent());
        }

        if (preference == mRotate180Pref) {
            value = mRotate180Pref.isChecked();
            if (mRotate180Pref.isChecked()) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION_MODE, 2);
            }
        }

        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

}
