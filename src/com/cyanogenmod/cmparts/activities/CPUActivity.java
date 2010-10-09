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

import java.io.File;

/**
 * CPU Related Settings
 */
public class CPUActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        private static final String GOVERNORS_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
        private static final String FREQ_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
        private static final String FREQ_MAX_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
        private static final String FREQ_MIN_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";

        /*
         * Read data from kernel
         */
        private String[] Governors = ReadOneLine(GOVERNORS_LIST_FILE).split(" ");
        private String[] Freqs = ReadOneLine(FREQ_LIST_FILE).split(" ");
        private String MinFreq = ReadOneLine(FREQ_MIN_FILE);
        private String MaxFreq = ReadOneLine(FREQ_MAX_FILE);

        /*
         * Create UI
         */
    }

    private String ReadOneLine(String fname) {
        FileInputStream file;
        DataInputStream in_file;
        BufferedReader br_file;
        String line = null;

        try {
            file = new FileInputStream(fname);
            in_file = new DataInputStream(file);
            br_file = new BufferedReader (new InputStreamReader(in_file));
            String line;

            line = br_file.readline();
            in_file.close(); 
        } catch (Exception e) {
        // Toast user about some probs
            line = null;
        }
    }
    
}
