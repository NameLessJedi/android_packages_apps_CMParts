package com.cyanogenmod.cmparts.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.cyanogenmod.cmparts.activities.CPUActivity;

public class ShutdownService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		/*
		 * Not working with IPC, we can return null.
		 */
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onCreate();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(CPUActivity.DIRTY_FLAG, false);
		editor.commit();
		stopSelf();
	}
}
