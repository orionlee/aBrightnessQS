package net.oldev.aBrightnessQS;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.os.Handler;
import android.service.quicksettings.TileService;

/**
 * A service that listens to changes in brightness to update BrightnessTileService.
 * It is started by:
 * - after the tile is added: covers initial installation case.
 * - start after boot: covers normal use case.
 * - upon clicking the tile / entering MainActivity screen: covers case re-installation, 
 *   where the existing running service is stopped, but not started again. 
 *   Hence the service is started again upon any user interaction.
 */
public class BrightnessTileUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // Not a bound service
        return null;
    }


    //
    // Actual service logic
    //

    private BrightnessManager mBrightnessManager = null;
    private BrightnessManager.BrightnessContentObserver mBrightnessContentObserver = null;
    
    @Override
    public void onCreate() {
        PLog.d("BrightnessTileUpdateService.onCreate()");        
    }

    @Override
    public void onDestroy() {
        PLog.d("BrightnessTileUpdateService.onDestroy()");                
        mBrightnessManager.unregisterOnChange(mBrightnessContentObserver);
    }
    

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PLog.d("BrightnessTileUpdateService.onStartCommand() - Received start id " + startId + ": " + intent);
        doStart();
        return Service.START_STICKY;
    }    
    private void doStart() {
        if (mBrightnessManager != null) {
            PLog.v("BrightnessTileUpdateService(): it has already been started. No-op");
            return;
        }

        mBrightnessManager = new BrightnessManager(this);
        mBrightnessContentObserver = 
            mBrightnessManager.new BrightnessContentObserver(new Handler(), 
                new BrightnessManager.ChangeListener() {
                    @Override 
                    public void onChange(int newBrightnessPct) {
                        PLog.v("BrightnessTileUpdateService - mBrightnessContentObserver.onChange(): " + newBrightnessPct);
                        // Update the tile by asking it to listen
                        TileService.requestListeningState(getApplicationContext(), 
                            new ComponentName(getApplicationContext(), BrightnessTileService.class));
                    }
                }
            );

        mBrightnessManager.registerOnChange(mBrightnessContentObserver);

    }

    /**
     * Convenience helper to start this background service, 
     * if it has not been started.
     */
    public static ComponentName start(Context ctx) {
        ComponentName res = ctx.startService(new Intent(ctx.getApplicationContext(), BrightnessTileUpdateService.class));
        PLog.v("BrightnessTileUpdateService.start(): %s", res);
        return res;
    }

    public static boolean stop(Context ctx) {
        boolean res = ctx.stopService(new Intent(ctx.getApplicationContext(), BrightnessTileUpdateService.class));
        PLog.v("BrightnessTileUpdateService.stop(): %s", res);
        return res;
    }

}