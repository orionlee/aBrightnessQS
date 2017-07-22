package net.oldev.aBrightnessQS;

import android.content.Context;

import android.util.Log; // TODO: to refactor

/**
 * Encapsulates the access to screen brightness
 */
public class BrightnessManager {
    public static final int BRIGHTNESS_AUTO = BrightnessSettingsModel.BRIGHTNESS_AUTO;

    // TODO: To refactor
    private void warn(String msg, Throwable t) {
        Log.w("BTS", msg, t);
    }

    private void debug(String msg) {
        Log.d("BTS", msg);
    }

    // Intended to be used sparringly
    private void verbose(String msg) {
        Log.v("BTS", msg);
    }

    
    private final Context mContext;
    public BrightnessManager(Context context) {
        mContext = context;
    }

    public int getPct() {
        int brightness = getRaw();
        int pct;
        if (brightness == BRIGHTNESS_AUTO) {
            pct = BRIGHTNESS_AUTO;
        } else {
            pct = SysBrightnessToUISliderPctConverter.sysBrightnessToUiPct(brightness);
        }
        // for debugging brightness - UI slider percentage
        verbose("BrightnessManager.getPct():  brightness=" + brightness + " ; pct=" + pct);

        return pct;
    }
    
    public boolean canSetPct() {
        return android.provider.Settings.System.canWrite(mContext);
    }

    /**
     * @param pct brightness percentage, {@link #BRIGHTNESS_AUTO} for auto 
     */
    public void setPct(int pct) {
        if (pct == BRIGHTNESS_AUTO) {
            setAuto();
            verbose("BrightnessManager.setPct(): auto");
        } else {
            int brightness = SysBrightnessToUISliderPctConverter.uiPctToSysBrightness(pct);
            // ensure minimum level of brightness (aka 0%)
            // does not translate to brightness == 0
            // as it might not work for some devices, or might completely black out the screen.
            if (brightness < 2) {
                brightness = 1;
            }
            setManual(brightness);
            verbose("BrightnessManager.setPct(): pct=" + pct + " ; brightness=" + brightness);
        }
    }

    private void setManual(int brightnessVal) {
        // TODO: assert 0 <= brightnessVal <= 255
        android.provider.Settings.System.putInt(mContext.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, brightnessVal);
        android.provider.Settings.System.putInt(mContext.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);            
    }

    private void setAuto() {
        android.provider.Settings.System.putInt(mContext.getContentResolver(),
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
                    mContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (auto == 0) {
                current = android.provider.Settings.System.getInt(
                    mContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
            } else {
                current = BRIGHTNESS_AUTO;
            }
        } catch (android.provider.Settings.SettingNotFoundException ex) {
            warn("BrightnessManager.get() error. Returning Maximum to be safe",  ex);
            current = 255;
        }
        return current;
    }

}
