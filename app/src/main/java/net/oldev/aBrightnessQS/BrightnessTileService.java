package net.oldev.aBrightnessQS;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
///import android.widget.Toast;

import java.util.Locale;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class BrightnessTileService
   extends TileService {

    private void debug(String msg) {
        String msgFinal = msg + "[ServiceState{locked: " + isLocked() + ", secure: " + isSecure() + " }]";

        Log.d("BTS", msgFinal);
        /// Toast not useful, as they won't be shown until after quick settings tile have been pulled
        /// Use logcat instead
        ///   adb logcat -s QST:V
        ///Toast.makeText(getApplicationContext(), msgFinal, Toast.LENGTH_LONG).show();      
    }


    // Encapsulates the access to screen brightness
    private class BrightnessManager {
        public static final int BRIGHTNESS_AUTO = 999;

        //@see https://stackoverflow.com/questions/18312609/change-the-system-brightness-programmatically
        public int getScreenBrightness() {
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
                debug("getScreenBrightness() error: " + ex.getMessage());
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

    /**
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        debug("Tile tapped");

        // TODO: implement code to change brigtness
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
        private static int msPrevBrightness = -1;

        private final BrightnessTileService parent;
        TileUpdater(BrightnessTileService parent) {
            this.parent = parent;
        }

        public void run() {
            int brightness = parent.mBrightnessMgr.getScreenBrightness();
            if (brightness == msPrevBrightness) {
                return;
            }
            parent.debug("Brightness change detected - prev: " + msPrevBrightness + " , current: " + brightness);

            Tile tile = parent.getQsTile();
            String newLabel;
            if (brightness != BrightnessManager.BRIGHTNESS_AUTO) {
                int brightnessPct = Math.round(100 * brightness / 255);

                newLabel = "Brightness: " + brightnessPct + "%";
                newLabel = String.format(Locale.US,
                        "%s: %s%%",
                        parent.getString(R.string.tile_label),
                        brightnessPct);
            } else {
                newLabel = String.format(Locale.US,
                        "%s: %s",
                        parent.getString(R.string.tile_label),
                        parent.getString(R.string.brightness_auto_label));
            }
                
            // update tile UI finally
            tile.setLabel(newLabel);
            tile.updateTile();

            msPrevBrightness = brightness;
        }
    }
    private final TileUpdater mTileUpdater = new TileUpdater(this);

}
