package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//
// CPU Related Settings
//
public class CPUActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String GOV_PREF = "pref_cpu_gov";
    private static final String GOVERNORS_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    private static final String GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";

    private static final String MIN_FREQ_PREF = "pref_freq_min";
    private static final String MAX_FREQ_PREF = "pref_freq_max";
    private static final String FREQ_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    private static final String FREQ_MAX_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    private static final String FREQ_MIN_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";

    private ListPreference GovPref;
    private ListPreference MinFreqPref;
    private ListPreference MaxFreqPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // Read data from kernel
        //
        private String[] Governors = ReadOneLine(GOVERNORS_LIST_FILE).split(" ");
        private String[] Freqs = ReadOneLine(FREQ_LIST_FILE).split(" ");
        private String MinFreq = ReadOneLine(FREQ_MIN_FILE);
        private String MaxFreq = ReadOneLine(FREQ_MAX_FILE);
        private String Gov = ReadOneLine(GOVERNOR);

        //
        // UI
        //
        setTitle(R.string.cpu_settings_title);
        addPreferencesFromResource(R.xml.cpu_settings);
        
        PreferenceScreen PrefScreen = getPreferenceScreen();

        setStringSummary("gov_selected", Gov);
        GovPref = (ListPreference) PrefScreen.findPreference(GOV_PREF);
        GovPref.setEntryValues(Governors);
        GovPref.setEntries(Governors);

        setStringSummary("freq_min", MinFreq);
        MinFreqPref = (ListPreference) PrefScreen.findPreference(MIN_FREQ_PREF);
        MinFreqPref.setEntryValues(Freqs);
        MinFreqPref.setEntries(Freqs);

        setStringSummary("freq_max", MaxFreq);
        MaxFreqPref = (ListPreference) PrefScreen.findPreference(MAX_FREQ_PREF);
        MaxFreqPref.setEntryValues(Freqs);
        MaxFreqPref.setEntires(Freqs);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == GovPref) {
            if (newValue != null) {
                setStringSummary("gov_selected", (String) newValue);
                return true;
            }
        }
        if (preference == MinFreqPref) {
            if (newValue != null) {
                setStringSummary("freq_min", (String) newValue);
                return true;
            }
        }
        if (preference == MaxFreqPref) {
            if (newValue != null) {
                setStringSummary("freq_max", (String) newValue);
                return true;
            }
        }
        return false;
    }

    private String ReadOneLine(String fname) {
        BufferedReader br;
        String line = null;

        try {
            br = new BufferedReader (new FileReader(fname), 512);
            try {
                line = br.readLine();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            Log.e("CPUSettings", "IO Exception when reading /sys/ file", e);
        }
        return line;
    }
}
