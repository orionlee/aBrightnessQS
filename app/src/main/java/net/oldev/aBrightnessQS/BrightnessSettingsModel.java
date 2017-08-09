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
    
    public static class IllegalSettingsException extends IllegalArgumentException {
        private final String mInvalidLevel;

        public IllegalSettingsException(String invalidLevel) {
            super(constructErrMsg(invalidLevel));
            mInvalidLevel = invalidLevel;
        }

        public IllegalSettingsException(String invalidLevel, Throwable t) {
            super(constructErrMsg(invalidLevel), t);
            mInvalidLevel = invalidLevel;
        }

        private static String constructErrMsg(String invalidLevel) {
            return "Invalid level: " + invalidLevel;
        }

        public String getInvalidLevel() { return mInvalidLevel; }
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
        throws IllegalSettingsException {

        if (settingsStr == null || settingsStr.isEmpty()) {
            throw new IllegalSettingsException("");
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
                    throw new IllegalSettingsException(Integer.toString(lvl));
                }
            } catch (IllegalSettingsException ise) {
                throw ise;
            } catch (Exception e) {
                throw new IllegalSettingsException(lvlStr, e);
            }            
        }
        // Ensure the levels are sorted, ascending.
        Arrays.sort(res);

        return res;
    }

    private static void validateSettingsStr(String settingsStr) 
        throws IllegalSettingsException {
        settingsStrToArray(settingsStr);
    }

    private static String  normalizeSettingsStr(String settingsStr) {
        final int[] settingsAry = settingsStrToArray(settingsStr);
        // Join the int[] to a comma-spearated string.
        // neither android TextUtils nor java8 String.join work on int[]
        final StringBuffer sb = new StringBuffer();
        sb.append(settingsAry[0]);
        for(int i = 1; i < settingsAry.length; i++) {
            sb.append(',');
            sb.append(settingsAry[i]);
        }
        return sb.toString();
    }

    public void setSettings(String settings) throws IllegalSettingsException {
        validateSettingsStr(settings);

        // normalize the string stored, mainly ensuring they are levels are sorted (ascending).
        // (Spaces will be removed too, but they do not matter either way.)
        // The normalized one will be shown to user right away, to reduce any user surprise.
        final String settingsNormalized = normalizeSettingsStr(settings);

        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(PREFS_BRIGHTNESS_LEVELS, settingsNormalized);
        final boolean success = editor.commit();
        if (success) {
            fireChangeEvent(settingsNormalized);
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
