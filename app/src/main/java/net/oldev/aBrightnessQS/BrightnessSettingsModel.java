package net.oldev.aBrightnessQS;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;

class BrightnessSettingsModel {
    
    private static final String PREFERENCES_KEY = "net.oldev.aBrightnessQS";
    private static final String PREFS_BRIGHTNESS_LEVELS = "brightnessLevels";
    private static final String PREFS_BRIGHTNESS_LEVELS_DEFAULTS = "10,30,50,75,100";
    
    // No need to use WeakReference<Context> pattern to hold Context per
    //   https://android-developers.googleblog.com/2009/01/avoiding-memory-leaks.html
    // because an instance's lifecycle is confined within the instantiator, 
    // (as a private member of the instantiator)
    // hence no memory leak.
    private final Context mContext;
    
    private ChangeListener mListener = null;
    
    public static final int BRIGHTNESS_AUTO = 999;
    public static interface ChangeListener {
        void onChange(String newSettings);
    }
    public BrightnessSettingsModel(Context context) {
        mContext = context;
    }
        
    private SharedPreferences getPrefs() {
        SharedPreferences prefs =
            mContext.getSharedPreferences(PREFERENCES_KEY,
                Context.MODE_PRIVATE);        
        return prefs;   
    }
    
    public String getSettings() {
        return getPrefs().getString(PREFS_BRIGHTNESS_LEVELS, 
            PREFS_BRIGHTNESS_LEVELS_DEFAULTS);
    }
    
    public int[] getSettingsAsArray() {
        return settingsStrToArray(getSettings());
    }
    static int[] settingsStrToArray(String settingsStr)
        throws IllegalArgumentException {

        if (settingsStr == null || settingsStr.isEmpty()) {
            throw new IllegalArgumentException("Brightness Level Settings must not be empty");
        }
        String[] lvlStrs = settingsStr.split(",");
        int[] res = new int[lvlStrs.length];
        for(int i = 0; i < lvlStrs.length; i++) {
            final String lvlStr = lvlStrs[i];
            try {
                final int lvl = Integer.parseInt(lvlStr.trim());
                if ( (lvl >= 1 && lvl <= 100) || lvl == BRIGHTNESS_AUTO ) {
                    res[i] = lvl;
                } else {
                    throw new IllegalArgumentException("Invalid level " + lvl);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Brightness Levels Settings must be comma-separated. " + 
                "Each level between 1 and 100 (or 999 for auto). Invalid one in question: " + lvlStr, e);
            }            
        }
        // TODO: decide what to do if levels given are not sorted.
        // for now, I sort the result automatically, rather than throwing exception
        Arrays.sort(res);

        return res;
    }

    private static void validateSettingsStr(String settingsStr) 
        throws IllegalArgumentException {
        settingsStrToArray(settingsStr);
    }
    public void setSettings(String settings) throws IllegalArgumentException {
        validateSettingsStr(settings);

        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(PREFS_BRIGHTNESS_LEVELS, settings);
        final boolean success = editor.commit();
        if (success) {
            fireChangeEvent(settings);
        } else {
            throw new RuntimeException("Unexpected failure in committing brightness settings.");
        }        
    }
    
    private void fireChangeEvent(String newSettings) {
        if (mListener != null) {
            mListener.onChange(newSettings);
        }
    }
    
    public void setOnChangeListener(ChangeListener listener) {
        mListener = listener;
        fireChangeEvent(getSettings());
    }
    
}
