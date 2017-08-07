package net.oldev.aBrightnessQS;

import android.content.Context;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import android.support.annotation.NonNull;

/**
* Encapsulates the access to screen brightness
*/
public class BrightnessManager {
    public static final int BRIGHTNESS_AUTO = BrightnessSettingsModel.BRIGHTNESS_AUTO;
    
    // No need to use WeakReference<Context> pattern to hold Context per
    //   https://android-developers.googleblog.com/2009/01/avoiding-memory-leaks.html
    // because an instance's lifecycle is confined within the instantiator, 
    // (as a private member of the instantiator)
    // hence no memory leak.
    private final Context mContext;
    private SysBrightnessToUISliderPctConverter mConverter;
    public BrightnessManager(Context context) {
        mContext = context;
        // TODO: make converter impl configurable
        mConverter = new SysBrightnessToUISliderPctConverterLinearImpl();
    }
    
    public int getPct() {
        int brightness = getRaw();
        int pct;
        if (brightness == BRIGHTNESS_AUTO) {
            pct = BRIGHTNESS_AUTO;
        } else {
            pct = mConverter.sysBrightnessToUiPct(brightness);
        }
        // for debugging brightness - UI slider percentage
        PLog.v("BrightnessManager.getPct():  brightness=%s ; pct=%s", brightness, pct);
        
        return pct;
    }
    
    public boolean canSetPct() {
        return Settings.System.canWrite(mContext);
    }
    
    /**
    * @param pct brightness percentage, {@link #BRIGHTNESS_AUTO} for auto 
    */
    public void setPct(int pct) {
        if (pct == BRIGHTNESS_AUTO) {
            setAuto();
            PLog.v("BrightnessManager.setPct(): auto");
        } else {
            int brightness = mConverter.uiPctToSysBrightness(pct);
            // ensure minimum level of brightness (aka 0%)
            // does not translate to brightness == 0
            // as it might not work for some devices, or might completely black out the screen.
            if (brightness < 2) {
                brightness = 1;
            }
            setManual(brightness);
            PLog.v("BrightnessManager.setPct(): pct=%s ; brightness=%s", pct, brightness);
        }
    }
    
    private void setManual(int brightnessVal) {
        assert 0 <= brightnessVal && brightnessVal <= 255; // should have been guaranteed by (internal) callers.
        Settings.System.putInt(mContext.getContentResolver(),
        Settings.System.SCREEN_BRIGHTNESS, brightnessVal);
        Settings.System.putInt(mContext.getContentResolver(),
        Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);            
    }
    
    private void setAuto() {
        Settings.System.putInt(mContext.getContentResolver(),
        Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);            
    }
    
    /**
    * @return raw brightness value (0 - 255)
    * @see https://stackoverflow.com/questions/18312609/change-the-system-brightness-programmatically
    */
    private int getRaw() {
        int current;
        try {
            int auto = Settings.System.getInt(
            mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (auto == 0) {
                current = Settings.System.getInt(
                mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } else {
                current = BRIGHTNESS_AUTO;
            }
        } catch (Settings.SettingNotFoundException ex) {
            PLog.w("BrightnessManager.get() error. Returning Maximum to be safe",  ex);
            current = 255;
        }
        return current;
    }
    
    //
    // Brightness change subscription
    // 

    public static interface ChangeListener {
        void onChange(int newBrightnessPct);
    }
    
    public void registerOnChange(BrightnessContentObserver observer) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri brightnessLevelUri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Uri brightnessModeUri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);

        // The observer trackes both the level and mode changes
        contentResolver.registerContentObserver(brightnessLevelUri, false, observer);
        contentResolver.registerContentObserver(brightnessModeUri, false, observer);

    }
    
    public void unregisterOnChange(BrightnessContentObserver observer) {
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.unregisterContentObserver(observer);
    }

    public class BrightnessContentObserver extends ContentObserver {
        
        private final ChangeListener mChangeListener;
        public BrightnessContentObserver(@NonNull Handler handler, @NonNull ChangeListener changeListener) {
            super(handler);
            mChangeListener = changeListener;
        }
        
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
        
        // For backward compatibility
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }
        
        // Implement the onChange(boolean, Uri) method to take advantage of the new Uri argument.
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            final int newBrightnessPct = BrightnessManager.this.getPct();
            mChangeListener.onChange(newBrightnessPct);
        } 
    }
}
