package net.oldev.aBrightnessQS;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import java.util.Locale;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class BrightnessTileService
   extends TileService {

    private final BrightnessManager mBrightnessMgr = new BrightnessManager(getApplicationContext());


    /**
     * Called when the tile is added to the Quick Settings.
     * @return TileService constant indicating tile state
     */

    @Override
    public void onTileAdded() {
        PLog.d("Tile added");
    }

    /**
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() {
        PLog.d("Start listening");

        mTileUpdater.run();
    }


    private BrightnessSettingsModel mSettingsModel;
    private int getNextLevelInPct(int curPct) {
        if (mSettingsModel == null) {
            mSettingsModel = new BrightnessSettingsModel(getApplicationContext());
        }
        final int[] steps = mSettingsModel.getSettingsAsArray();

        for(int i = 0; i < steps.length; i++) {
            // add a - 1 to account for the rounding error somtimes introduced.
            // E.g., setting at UI slider 10%, the raw brightness that got 
            // translated back could be 9%, 
            // If we do not minue one in calculate the next step,
            // it will get perpectually stuck at 10% level.
            if (curPct < steps[i] - 1) {
                return steps[i];
            }
        }
        // case curPct >= max(steps), rotate back to the front
        return steps[0];
    }

    /**
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        PLog.d("Tile tapped");

        if (mBrightnessMgr.canSetPct()) {            
            int pctToSet = getNextLevelInPct(mBrightnessMgr.getPct()); 
            mBrightnessMgr.setPct(pctToSet); 
            mTileUpdater.run(); 
        } else {
            // TODO: launch a request screen 
            // 1. Use tile.startActivityAndCollapse(Intent) to launch request screen
            //    @see https://developer.android.com/reference/android/service/quicksettings/TileService.html#startActivityAndCollapse(android.content.Intent)
            // 2. implmenet the request screen
            //    @see https://developer.android.com/training/permissions/requesting.html
            // 
            // A quick and dirty is to launch com.android.settings.Settings$WriteSettingsActivity for the screen
            String permissionNeededMsg = "Modify system settings permission required. Please goto Settings > App permissions > Special access > Modify system settings to grant the access";
            android.widget.Toast.makeText(getApplicationContext(), permissionNeededMsg, 
                    android.widget.Toast.LENGTH_LONG).show();      
        }      
    }

    /**
     * Called when this tile moves out of the listening state.
     */
    @Override
    public void onStopListening() {
        PLog.d("Stop Listening");
    }

    /**
     * Called when the user removes this tile from Quick Settings.
     */
    @Override
    public void onTileRemoved() {
        PLog.d("Tile removed");
    }


    // Encapsulates the logic to change the tile UI.
    private static class TileUpdater {
        // Used exclusively by updateTile()
        // a quasi-persistent cache of previous brightness value, 
        // (that persists across multiple object instance creation/deletion),
        // but without the performance penalty of relying on actual persistence.
        private static int msPrevBrightnessPct = -1;

        private final BrightnessTileService parent;
        TileUpdater(BrightnessTileService parent) {
            this.parent = parent;
        }

        // Convert brightness percentage to 
        // an icon similar to what user preceives with standard brightness slider bar
        // With standard brightness slider bar, 
        // - 1-quarter is about 5%
        // - roughly  half-way is about 25%,
        // - 3-quarter is about 50%)
        private int brightnessPctToIconRsrcId(int brightnessPct) {
            if (brightnessPct > 85) {
                return R.drawable.tile_brightness_black_100_24dp;
            } else if (brightnessPct > 65) {
                return R.drawable.tile_brightness_black_75_24dp;                
            } else if (brightnessPct > 40) {
                return R.drawable.tile_brightness_black_50_24dp;                
            } else if (brightnessPct > 24) {
                return R.drawable.tile_brightness_black_25_24dp;                
            } else {
                return R.drawable.tile_brightness_black_0_24dp;                                
            }
        }
        public void run() {
            int brightnessPct = parent.mBrightnessMgr.getPct();
            if (brightnessPct == msPrevBrightnessPct) {
                PLog.v("TileUpdater: no change in brightness. No Update.");
                return;
            }
            PLog.d("TileUpdater: Brightness% change detected - prev: " + msPrevBrightnessPct + " , current: " + brightnessPct);

            // TODO: consider to supply an user option of not updating tile
            // if the intermittent stalling (with multiple clicks) cannot be fixed

            Tile tile = parent.getQsTile();

            int newIconRsrcId;
            if (brightnessPct != BrightnessManager.BRIGHTNESS_AUTO) {
                newIconRsrcId = brightnessPctToIconRsrcId(brightnessPct);
            } else {
                newIconRsrcId = R.drawable.tile_brightness_black_auto_24dp;
            }
             
            // update tile UI finally
            tile.setIcon(Icon.createWithResource(parent.getApplicationContext(), newIconRsrcId));
            tile.updateTile();

            // remember current level for subequent uses
            msPrevBrightnessPct = brightnessPct;
        }
    }
    private final TileUpdater mTileUpdater = new TileUpdater(this);

}
