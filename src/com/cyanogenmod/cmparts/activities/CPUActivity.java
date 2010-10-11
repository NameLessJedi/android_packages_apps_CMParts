package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

// import android.app.AlertDialog;
// import android.content.DialogInterface;
import android.os.Bundle;
// import android.os.SystemProperties;
// import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
// import android.provider.Settings;
import android.util.Log;

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
    private Preference GovSel;
    private Preference FreqMin;
    private Preference FreqMax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // Read data from kernel
        //
        String[] Governors = ReadOneLine(GOVERNORS_LIST_FILE).split(" ");
        String[] Freqs = ReadOneLine(FREQ_LIST_FILE).split(" ");
        String MinFreq = ReadOneLine(FREQ_MIN_FILE);
        String MaxFreq = ReadOneLine(FREQ_MAX_FILE);
        String Gov = ReadOneLine(GOVERNOR);

        //
        // UI
        //
        setTitle("CPU Settings");
        addPreferencesFromResource(R.xml.cpu_settings);
        
        PreferenceScreen PrefScreen = getPreferenceScreen();

        GovSel = PrefScreen.findPreference("gov_selected");
        GovSel.setSummary(Gov);

        GovPref = (ListPreference) PrefScreen.findPreference(GOV_PREF);
        GovPref.setEntryValues(Governors);
        GovPref.setEntries(Governors);

        FreqMin = PrefScreen.findPreference("freq_min");
        FreqMin.setSummary(MinFreq);

        MinFreqPref = (ListPreference) PrefScreen.findPreference(MIN_FREQ_PREF);
        MinFreqPref.setEntryValues(Freqs);
        MinFreqPref.setEntries(Freqs);

        FreqMax = PrefScreen.findPreference("freq_max");
        FreqMax.setSummary(MaxFreq);

        MaxFreqPref = (ListPreference) PrefScreen.findPreference(MAX_FREQ_PREF);
        MaxFreqPref.setEntryValues(Freqs);
        MaxFreqPref.setEntries(Freqs);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == GovPref) {
            if (newValue != null) {
                GovSel.setSummary((String) newValue);
                return true;
            }
        }
        if (preference == MinFreqPref) {
            if (newValue != null) {
                FreqMin.setSummary((String) newValue);
                return true;
            }
        }
        if (preference == MaxFreqPref) {
            if (newValue != null) {
                FreqMax.setSummary((String) newValue);
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
