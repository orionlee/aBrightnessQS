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
 * Listen to changes in brightness to update BrightnessTileService
 */
public class BrightnessTileUpdateService extends Service {

    //
    // Plumbing for local service
    //

    public class LocalBinder extends Binder {
        BrightnessTileUpdateService getService() {
            return BrightnessTileUpdateService.this;
        }
    }
    
    // The object that receives interactions from local clients. 
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: maybe throw UnsupportedException instead as it is not intended to be a Bound service?
        return mBinder;
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

}