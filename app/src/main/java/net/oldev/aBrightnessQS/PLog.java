package net.oldev.aBrightnessQS;

import android.util.Log;

/**
 * Log wrapper to standardize on the log messages emitted.
 */
public class PLog {
    private PLog() {} // only has static methods
    private static final String TAG = "BrightnessTile";
    
    public static void w(String msg, Throwable t) {
        Log.w(TAG, msg, t);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }
    

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void d(String msgFormat, Object... args) {
        final String msg= String.format(msgFormat, args);
        d(msg);
    }

    // Intended to be used sparingly
    public static void v(String msg) {
        if (BuildConfig.DEBUG) { // only used for development
            Log.v(TAG, msg);
        }
    }

    public static void v(String msgFormat, Object... args) {
        final String msg= String.format(msgFormat, args);
        v(msg);
    }

}