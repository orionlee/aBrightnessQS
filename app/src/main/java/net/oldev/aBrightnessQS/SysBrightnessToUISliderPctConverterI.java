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
public interface SysBrightnessToUISliderPctConverterI {

    public int uiPctToSysBrightness(int uiPct);

    public int sysBrightnessToUiPct(int sysBrightness);
}