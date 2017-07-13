package net.oldev.aBrightnessQS;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import java.util.Locale;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class BrightnessTileService
   extends TileService {

    private void debug(String msg) {
        Log.d("BTS", msg);
    }


    // Encapsulates the access to screen brightness
    private class BrightnessManager {
        public static final int BRIGHTNESS_AUTO = 999;

        public int getPct() {
            int brightness = getRaw();
            if (brightness == BRIGHTNESS_AUTO) {
                return BRIGHTNESS_AUTO;
            } else {
                return Math.round(100.0f * brightness / 255);
            }
        }
        
        public boolean canSetPct() {
            return android.provider.Settings.System.canWrite(getApplicationContext());
        }

        /**
         * @param pct brightness percentage, {@link #BRIGHTNESS_AUTO} for auto 
         */
        public void setPct(int pct) {
            if (pct == BRIGHTNESS_AUTO) {
                setAuto();
            } else {
                int brightness = Math.round(255.0f * pct / 100);
                // ensure minimum level of brightness (aka 0%)
                // does not translate to brightness == 0
                // as it might not work for some devices, or might completely black out the screen.
                if (brightness < 3) {
                    brightness = 2;
                }
                setManual(brightness);
            }
        }

        private void setManual(int brightnessVal) {
            // TODO: assert 0 <= brightnessVal <= 255
            android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, brightnessVal);
            android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);            
        }

        private void setAuto() {
            android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);            
        }

        /**
         * @return raw brightness value (0 - 255)
         * @see https://stackoverflow.com/questions/18312609/change-the-system-brightness-programmatically
         */
        private int getRaw() {
            int current;
            try {
                int auto = android.provider.Settings.System.getInt(
                        getApplicationContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE);
                if (auto == 0) {
                    current = android.provider.Settings.System.getInt(
                        getApplicationContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                } else {
                    current = BRIGHTNESS_AUTO;
                }
            } catch (android.provider.Settings.SettingNotFoundException ex) {
                debug("BrightnessManager.get() error: " + ex.getMessage());
                current = -1;
            }
            return current;
        }

    }
    private final BrightnessManager mBrightnessMgr = new BrightnessManager();


    /**
     * Called when the tile is added to the Quick Settings.
     * @return TileService constant indicating tile state
     */

    @Override
    public void onTileAdded() {
        debug("Tile added");
    }

    /**
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() {
        debug("Start listening");

        mTileUpdater.run();
    }


    private int getNextLevelInPct(int curPct) {
        // TODO: make it user-configurable
        // MUST be sorted
        final int[] steps = {1, 5, 10, 25, 35, 55, 100}; // , BrightnessManager.BRIGHTNESS_AUTO

        for(int i = 0; i < steps.length; i++) {
            if (curPct < steps[i]) {
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
        debug("Tile tapped");

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
        debug("Stop Listening");
    }

    /**
     * Called when the user removes this tile from Quick Settings.
     */
    @Override
    public void onTileRemoved() {
        debug("Tile removed");
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
            if (brightnessPct > 75) {
                return R.drawable.tile_brightness_black_100_24dp;
            } else if (brightnessPct > 40) {
                return R.drawable.tile_brightness_black_75_24dp;                
            } else if (brightnessPct > 11) {
                return R.drawable.tile_brightness_black_50_24dp;                
            } else if (brightnessPct > 4) {
                return R.drawable.tile_brightness_black_25_24dp;                
            } else {
                return R.drawable.tile_brightness_black_0_24dp;                                
            }
        }
        public void run() {
            int brightnessPct = parent.mBrightnessMgr.getPct();
            if (brightnessPct == msPrevBrightnessPct) {
                return;
            }
            parent.debug("Brightness% change detected - prev: " + msPrevBrightnessPct + " , current: " + brightnessPct);

            Tile tile = parent.getQsTile();
            String newLabel;
            int newIconRsrcId;
            if (brightnessPct != BrightnessManager.BRIGHTNESS_AUTO) {

                newLabel = "Brightness: " + brightnessPct + "%";
                newLabel = String.format(Locale.US,
                        "%s: %s%%",
                        parent.getString(R.string.tile_label),
                        brightnessPct);
                newIconRsrcId = brightnessPctToIconRsrcId(brightnessPct);
            } else {
                newLabel = String.format(Locale.US,
                        "%s: %s",
                        parent.getString(R.string.tile_label),
                        parent.getString(R.string.brightness_auto_label));
                newIconRsrcId = R.drawable.tile_brightness_black_auto_24dp;
            }
             
            // update tile UI finally
            tile.setLabel(newLabel);
            tile.setIcon(Icon.createWithResource(parent.getApplicationContext(), newIconRsrcId));
            tile.setState(Tile.STATE_ACTIVE); // typically no need, but do it nonetheless as a defensive measure.
            tile.updateTile();

            // remember current level for subequent uses
            msPrevBrightnessPct = brightnessPct;
        }
    }
    private final TileUpdater mTileUpdater = new TileUpdater(this);

}
