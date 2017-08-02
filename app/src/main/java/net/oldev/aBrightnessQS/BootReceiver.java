package net.oldev.aBrightnessQS;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import net.oldev.aBrightnessQS.BrightnessTileUpdateService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            PLog.d("BootReceiver.onReceive() - starting BrightnessTileUpdateService.");

            // see BrightnessTileUpdateService for the service's starting points
            BrightnessTileUpdateService.start(context);
            
        } else {
            PLog.w("BootReceiver.onReceive() - unexpected intent, do nothing:  " + intent);
        }
    }
}