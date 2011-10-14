package com.cyanogenmod.cmparts.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.ActivityNotFoundException;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.utils.ShortcutPickHelper;

import java.io.File;
import java.util.ArrayList;

public class LockscreenActivity extends PreferenceActivity implements
        OnPreferenceChangeListener, ShortcutPickHelper.OnPickListener {

    private static final String LOCKSCREEN_MUSIC_CONTROLS = "lockscreen_music_controls";
    private static final String LOCKSCREEN_ALWAYS_MUSIC_CONTROLS = "lockscreen_always_music_controls";
    private static final String TRACKBALL_UNLOCK_PREF = "pref_trackball_unlock";
    private static final String MENU_UNLOCK_PREF = "pref_menu_unlock";
    private static final String BUTTON_CATEGORY = "pref_category_button_settings";
    private static final String LOCKSCREEN_STYLE_PREF = "pref_lockscreen_style";
    private static final String LOCKSCREEN_QUICK_UNLOCK_CONTROL = "lockscreen_quick_unlock_control";
    private static final String LOCKSCREEN_CUSTOM_APP_TOGGLE = "pref_lockscreen_custom_app_toggle";
    private static final String LOCKSCREEN_CUSTOM_APP_ACTIVITY = "pref_lockscreen_custom_app_activity";
    private static final String LOCKSCREEN_DISABLE_UNLOCK_TAB = "lockscreen_disable_unlock_tab";
    private static final String LOCKSCREEN_ALWAYS_BATTERY = "lockscreen_always_battery";

    private CheckBoxPreference mMusicControlPref;
    private CheckBoxPreference mAlwaysMusicControlPref;
    private CheckBoxPreference mTrackballUnlockPref;
    private CheckBoxPreference mMenuUnlockPref;
    private CheckBoxPreference mQuickUnlockScreenPref;
    private CheckBoxPreference mCustomAppTogglePref;
    private CheckBoxPreference mDisableUnlockTab;
    private CheckBoxPreference mBatteryAlwaysOnPref;

    private ListPreference mLockscreenStylePref;
    private ShortcutPickHelper mPicker;

    private Preference mCustomAppActivityPref;

    /* Screen Lock */
    private static final String LOCKSCREEN_TIMEOUT_DELAY_PREF = "pref_lockscreen_timeout_delay";
    private static final String LOCKSCREEN_SCREENOFF_DELAY_PREF = "pref_lockscreen_screenoff_delay";

    private ListPreference mScreenLockTimeoutDelayPref;
    private ListPreference mScreenLockScreenOffDelayPref;

    private int mLockscreenStyle;

    private int mMaxRingCustomApps = Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES.length;
    private int mWhichApp = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_settings_title_subhead);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mLockscreenStyle = Settings.System.getInt(getContentResolver(), Settings.System.LOCKSCREEN_STYLE_PREF, 1);

        /* Always display battery perceantage */
        mBatteryAlwaysOnPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALWAYS_BATTERY);
        mBatteryAlwaysOnPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_ALWAYS_BATTERY, 0) == 1);
                
        /* Music Controls */
        mMusicControlPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_MUSIC_CONTROLS);
        mMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_MUSIC_CONTROLS, 0) == 1);

        /* Always Display Music Controls */
        mAlwaysMusicControlPref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_ALWAYS_MUSIC_CONTROLS);
        mAlwaysMusicControlPref.setChecked(Settings.System.getInt(getContentResolver(), 
                Settings.System.LOCKSCREEN_ALWAYS_MUSIC_CONTROLS, 0) == 1);

        /* Quick Unlock Screen Control */
        mQuickUnlockScreenPref = (CheckBoxPreference)
                prefSet.findPreference(LOCKSCREEN_QUICK_UNLOCK_CONTROL);
        mQuickUnlockScreenPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, 0) == 1);

        /* Lockscreen Custom App Toggle */
        mCustomAppTogglePref = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_CUSTOM_APP_TOGGLE);
        mCustomAppTogglePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_CUSTOM_APP_TOGGLE, 0) == 1);


        /* Lockscreen Style */
        mLockscreenStylePref = (ListPreference) prefSet.findPreference(LOCKSCREEN_STYLE_PREF);
        mLockscreenStylePref.setValue(String.valueOf(mLockscreenStyle));
        mLockscreenStylePref.setOnPreferenceChangeListener(this);

        /* Trackball Unlock */
        mTrackballUnlockPref = (CheckBoxPreference) prefSet.findPreference(TRACKBALL_UNLOCK_PREF);
        mTrackballUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_UNLOCK_SCREEN, 0) == 1);
        /* Menu Unlock */
        mMenuUnlockPref = (CheckBoxPreference) prefSet.findPreference(MENU_UNLOCK_PREF);
        mMenuUnlockPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.MENU_UNLOCK_SCREEN, 0) == 1);

        /* Disabling of unlock tab on lockscreen */
        mDisableUnlockTab = (CheckBoxPreference)
        prefSet.findPreference(LOCKSCREEN_DISABLE_UNLOCK_TAB);
        if (!doesUnlockAbilityExist()) {
            mDisableUnlockTab.setEnabled(false);
            mDisableUnlockTab.setChecked(false);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GESTURES_DISABLE_UNLOCK, 0);
        } else {
            mDisableUnlockTab.setEnabled(true);
        }

        PreferenceCategory buttonCategory = (PreferenceCategory)prefSet.findPreference(BUTTON_CATEGORY);

        if (!getResources().getBoolean(R.bool.has_trackball)) {
            buttonCategory.removePreference(mTrackballUnlockPref);
        }

        mCustomAppActivityPref = (Preference) prefSet.findPreference(LOCKSCREEN_CUSTOM_APP_ACTIVITY);
        
        /* Screen Lock */
        mScreenLockTimeoutDelayPref = (ListPreference) prefSet
                .findPreference(LOCKSCREEN_TIMEOUT_DELAY_PREF);
        int timeoutDelay = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_LOCK_TIMEOUT_DELAY, 5000);
        mScreenLockTimeoutDelayPref.setValue(String.valueOf(timeoutDelay));
        mScreenLockTimeoutDelayPref.setOnPreferenceChangeListener(this);

        mScreenLockScreenOffDelayPref = (ListPreference) prefSet
                .findPreference(LOCKSCREEN_SCREENOFF_DELAY_PREF);
        int screenOffDelay = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_LOCK_SCREENOFF_DELAY, 0);
        mScreenLockScreenOffDelayPref.setValue(String.valueOf(screenOffDelay));
        mScreenLockScreenOffDelayPref.setOnPreferenceChangeListener(this);
        mPicker = new ShortcutPickHelper(this, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLockscreenStyle == 5) {
            mCustomAppActivityPref.setSummary(getCustomRingAppSummary());
        } else {
            String value = Settings.System.getString(getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_APP_ACTIVITY);
            mCustomAppActivityPref.setSummary(mPicker.getFriendlyNameForUri(value));
        }

        if (!doesUnlockAbilityExist()) {
            mDisableUnlockTab.setEnabled(false);
            mDisableUnlockTab.setChecked(false);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GESTURES_DISABLE_UNLOCK, 0);
        } else {
            mDisableUnlockTab.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPicker.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mBatteryAlwaysOnPref) {
            value = mBatteryAlwaysOnPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ALWAYS_BATTERY, value ? 1 : 0);
        } else if (preference == mMusicControlPref) {
            value = mMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mAlwaysMusicControlPref) {
            value = mAlwaysMusicControlPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ALWAYS_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mQuickUnlockScreenPref) {
            value = mQuickUnlockScreenPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mCustomAppTogglePref) {
            value = mCustomAppTogglePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_APP_TOGGLE, value ? 1 : 0);
            return true;
        } else if (preference == mTrackballUnlockPref) {
            value = mTrackballUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.TRACKBALL_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mMenuUnlockPref) {
            value = mMenuUnlockPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MENU_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        } else if (preference == mDisableUnlockTab) {
            value = mDisableUnlockTab.isChecked();
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GESTURES_DISABLE_UNLOCK, value ? 1 : 0);
        } else if (preference == mCustomAppActivityPref) {
            if (mLockscreenStyle == 5) {
                final String[] items = getCustomRingAppItems();

                if (items.length == 0) {
                    mWhichApp = 0;
                    mPicker.pickShortcut();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.pref_lockscreen_ring_custom_apps_dialog_title_set);
                    builder.setItems(items, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mWhichApp = which;
                            mPicker.pickShortcut();
                        }
                    });
                    if (items.length < mMaxRingCustomApps) {
                        builder.setPositiveButton(R.string.pref_lockscreen_ring_custom_apps_dialog_add,
                                new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mWhichApp = items.length;
                                mPicker.pickShortcut();
                            }
                        });
                    }
                    builder.setNeutralButton(R.string.pref_lockscreen_ring_custom_apps_dialog_remove,
                            new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LockscreenActivity.this);
                            builder.setTitle(R.string.pref_lockscreen_ring_custom_apps_dialog_title_unset);
                            builder.setItems(items, new Dialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Settings.System.putString(getContentResolver(),
                                        Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[which], null);
                                    for (int q = which + 1; q < mMaxRingCustomApps; q++) {
                                        Settings.System.putString(getContentResolver(),
                                            Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q - 1],
                                            Settings.System.getString(getContentResolver(),
                                                Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q]));
                                        Settings.System.putString(getContentResolver(),
                                            Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q], null);
                                    }
                                    mCustomAppActivityPref.setSummary(getCustomRingAppSummary());
                                }
                            });
                            builder.setNegativeButton(R.string.pref_lockscreen_ring_custom_apps_dialog_cancel,
                                    new Dialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setCancelable(true);
                            builder.create().show();
                        }
                    });
                    builder.setNegativeButton(R.string.pref_lockscreen_ring_custom_apps_dialog_cancel,
                            new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(true);
                    builder.create().show();
                }
            } else {
                mPicker.pickShortcut();
            }
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenStylePref) {
            int lockscreenStyle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_STYLE_PREF,
                    lockscreenStyle);
            return true;
        } else if (preference == mScreenLockTimeoutDelayPref) {
            int timeoutDelay = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_LOCK_TIMEOUT_DELAY,
                    timeoutDelay);
            return true;
        } else if (preference == mScreenLockScreenOffDelayPref) {
            int screenOffDelay = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_LOCK_SCREENOFF_DELAY, screenOffDelay);
            return true;
        }
        return false;
    }

    @Override
    public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
        if (mWhichApp == -1) {
            if (Settings.System.putString(getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_APP_ACTIVITY, uri)) {
                mCustomAppActivityPref.setSummary(friendlyName);
            }
        } else {
            Settings.System.putString(getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[mWhichApp], uri);
            mCustomAppActivityPref.setSummary(getCustomRingAppSummary());
            mWhichApp = -1;
        }
    }

    private boolean doesUnlockAbilityExist() {
        final File mStoreFile = new File(Environment.getDataDirectory(), "/misc/lockscreen_gestures");
        boolean GestureCanUnlock = false;
        boolean trackCanUnlock = Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_UNLOCK_SCREEN, 0) == 1;
        boolean menuCanUnlock = Settings.System.getInt(getContentResolver(),
                Settings.System.MENU_UNLOCK_SCREEN, 0) == 1;
        GestureLibrary gl = GestureLibraries.fromFile(mStoreFile);
        if (gl.load()) {
            for (String name : gl.getGestureEntries()) {
                if ("UNLOCK___UNLOCK".equals(name)) {
                    GestureCanUnlock = true;
                    break;
                }
            }
        }
        if (GestureCanUnlock || trackCanUnlock || menuCanUnlock) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDefaultLockscreenStyle() {
        int lockscreenStyle = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_STYLE_PREF, 1);
        if (lockscreenStyle == 1) {
            return true;
        } else {
            return false;
        }
    }

    private String getCustomRingAppSummary() {
        String summary = "";
        String[] items = getCustomRingAppItems();

        for (int q = 0; q < items.length; q++) {
            if (q != 0) {
                summary += ", ";
            }
            summary += items[q];
        }
        return summary;
    }

    private String[] getCustomRingAppItems() {
        ArrayList<String> items = new ArrayList<String>();
        for (int q = 0; q < mMaxRingCustomApps; q++) {
            String uri = Settings.System.getString(getContentResolver(),
                         Settings.System.LOCKSCREEN_CUSTOM_RING_APP_ACTIVITIES[q]);
            if (uri != null) {
                items.add(mPicker.getFriendlyNameForUri(uri));
            }
        }
       return items.toArray(new String[0]);
    }
}
