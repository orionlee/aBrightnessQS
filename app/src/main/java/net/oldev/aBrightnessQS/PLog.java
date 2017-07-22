package net.oldev.aBrightnessQS;

import android.util.Log;

/**
 * Log wrapper to standardize on the log messages emitted.
 */
public class PLog {
    private PLog() {} // only has static methods

    public static void w(String msg, Throwable t) {
        Log.w("BTS", msg, t);
    }

    // TODO: add helpers that have parametrized string so that message
    // need not be built.
    
    // TODO: check BuildConfig to avoid debug (or even verbose) if it is not DEBUG
    public static void d(String msg) {
        Log.d("BTS", msg);
    }

    // Intended to be used sparringly
    public static void v(String msg) {
        Log.v("BTS", msg);
    }
    
}