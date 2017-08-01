package net.oldev.aBrightnessQS;

import static java.lang.Math.round;

/**
 * Conversion between system brightness to UI slider percentage 
 * for devices of which the relationship is simple linear one.
 */
public class SysBrightnessToUISliderPctConverterLinearImpl implements SysBrightnessToUISliderPctConverterI {

    public SysBrightnessToUISliderPctConverterLinearImpl() {}


    public int uiPctToSysBrightness(int uiPct) {
        validateUiPct(uiPct, "uiPctToSysBrightness()");

        final int sysBrightness = (int)round(uiPct * 255d / 100d);
 
        return sysBrightness;
    }
    public int sysBrightnessToUiPct(int sysBrightness) {
        validateSysBrightness(sysBrightness, "sysBrightnessToUiPct()");

        // convert V to 0-100 percentage
        final int uiPct = (int)round(sysBrightness / 255d * 100d);

        return uiPct;
    }
  
    private static void validateUiPct(int uiPct, String errMsgPrefix) {
        if (uiPct < 0 || uiPct > 100) {
            throw new IllegalArgumentException(errMsgPrefix + " uiPct must be an int between 0 and 100. Actual:<" + uiPct + ">");
        }
    }

    private static void validateSysBrightness(int sysBrightness, String errMsgPrefix) {
        if (sysBrightness < 0 || sysBrightness > 255) {
            throw new IllegalArgumentException(errMsgPrefix + " sysBrightness must be an int between 0 and 255. Actual:<" + sysBrightness + ">");
        }        
    }
    
}