package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Process;

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
    private static final String FREQ_MAX_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    private static final String FREQ_MIN_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    private static final String LOGTAG = "CPUSettings";

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

        //
        // UI
        //
        setTitle("CPU Settings");
        addPreferencesFromResource(R.xml.cpu_settings);
        
        PreferenceScreen PrefScreen = getPreferenceScreen();

        GovSel = PrefScreen.findPreference("gov_selected");
        GovSel.setSummary(ReadOneLine(GOVERNOR));

        GovPref = (ListPreference) PrefScreen.findPreference(GOV_PREF);
        GovPref.setEntryValues(Governors);
        GovPref.setEntries(Governors);
        GovPref.setOnPreferenceChangeListener(this);

        FreqMin = PrefScreen.findPreference("freq_min");
        FreqMin.setSummary(ReadOneLine(FREQ_MIN_FILE));

        MinFreqPref = (ListPreference) PrefScreen.findPreference(MIN_FREQ_PREF);
        MinFreqPref.setEntryValues(Freqs);
        MinFreqPref.setEntries(Freqs);
        MinFreqPref.setOnPreferenceChangeListener(this);

        FreqMax = PrefScreen.findPreference("freq_max");
        FreqMax.setSummary(ReadOneLine(FREQ_MAX_FILE));

        MaxFreqPref = (ListPreference) PrefScreen.findPreference(MAX_FREQ_PREF);
        MaxFreqPref.setEntryValues(Freqs);
        MaxFreqPref.setEntries(Freqs);
        MaxFreqPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        FreqMax.setSummary(ReadOneLine(FREQ_MAX_FILE));
        MaxFreqPref.setValue(ReadOneLine(FREQ_MAX_FILE));
        FreqMin.setSummary(ReadOneLine(FREQ_MIN_FILE));
        MinFreqPref.setValue(ReadOneLine(FREQ_MIN_FILE));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String rootcmd;

        if (preference == GovPref) {
            if (newValue != null) {
                rootcmd = "echo " + (String) newValue + " > " + GOVERNOR + "\n";
                try {
                    RootInvoker(rootcmd);
                    GovSel.setSummary((String) newValue);
                } catch (IOException e) {
                    Log.e(LOGTAG, "RootInvoker IOException", e);
                }
                return true;
            }
        }
        if (preference == MinFreqPref) {
            if (newValue != null) {
                rootcmd = "echo " + (String) newValue + " > " + FREQ_MIN_FILE + "\n";
                try {
                    RootInvoker(rootcmd);
                    FreqMin.setSummary((String) newValue);
                } catch (IOException e) {
                    Log.e(LOGTAG, "RootInvoker IOException", e);
                }
                return true;
            }
        }
        if (preference == MaxFreqPref) {
            if (newValue != null) {
                rootcmd = "echo " + (String) newValue + " > " + FREQ_MAX_FILE + "\n";
                try {
                    RootInvoker(rootcmd);
                    FreqMax.setSummary((String) newValue);
                } catch (IOException e) {
                    Log.e(LOGTAG, "RootInvoker IOException", e);
                }
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
            Log.e(LOGTAG, "IO Exception when reading /sys/ file", e);
        }
        return line;
    }

    private void RootInvoker(String rootCommand) throws IOException {

        Process process;
        BufferedWriter stdin;

        process = new ProcessBuilder("su").start();
        stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()), 512);
        stdin.write(rootCommand);
        stdin.close();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Log.e(LOGTAG,"Interrupted on waitFor()", e);
        }
    }
}
