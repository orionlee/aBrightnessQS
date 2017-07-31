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
            Intent startServiceIntent = new Intent(context, BrightnessTileUpdateService.class);
            ComponentName res = context.startService(startServiceIntent);
            PLog.d("BootReceiver.onReceive() - starting BrightnessTileUpdateServie: " + res);
        } else {
            PLog.w("BootReceiver.onReceive() - unexpected intent, do nothing:  " + intent);
        }
    }
}