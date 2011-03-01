package com.cyanogenmod.cmparts.intents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cyanogenmod.cmparts.services.CPUService;
import com.cyanogenmod.cmparts.services.ShutdownService;

public class CPUReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent _intent = new Intent(ctx, CPUService.class);
            ctx.startService(_intent);
        } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
            Intent _intent = new Intent(ctx, ShutdownService.class);
            ctx.startService(_intent);
        }
    }
}
