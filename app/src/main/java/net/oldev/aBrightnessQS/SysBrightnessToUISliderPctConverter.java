package net.oldev.aBrightnessQS;

/**
 * Interface for Utility to convert between:
 * - SysBrightness, i.e., screen brightness as defined by android API 
 *   android.provider.Settings.System.SCREEN_BRIGHTNESS
 * - UI Percentage as represented by system default screen brightness slider UI
 * 
 * Different Implementations adapt to different device's 
 * default screen brightness slider UI behavior.
 */
public abstract class SysBrightnessToUISliderPctConverter {

    public final int uiPctToSysBrightness(int uiPct) {
        validateUiPct(uiPct, "uiPctToSysBrightness()");
        return doUiPctToSysBrightness(uiPct);
    }

    public final int sysBrightnessToUiPct(int sysBrightness) {
        validateSysBrightness(sysBrightness, "sysBrightnessToUiPct()");
        return doSysBrightnessToUiPct(sysBrightness);
    }

    protected abstract int doUiPctToSysBrightness(int uiPct);

    protected abstract int doSysBrightnessToUiPct(int sysBrightness);

    private void validateUiPct(int uiPct, String errMsgPrefix) {
        if (uiPct < 0 || uiPct > 100) {
            throw new IllegalArgumentException(errMsgPrefix + " uiPct must be an int between 0 and 100. Actual:<" + uiPct + ">");
        }
    }

    private void validateSysBrightness(int sysBrightness, String errMsgPrefix) {
        if (sysBrightness < 0 || sysBrightness > 255) {
            throw new IllegalArgumentException(errMsgPrefix + " sysBrightness must be an int between 0 and 255. Actual:<" + sysBrightness + ">");
        }        
    }
        
}