package net.oldev.aBrightnessQS;

import static java.lang.Math.round;
import static java.lang.Math.pow;
import static java.lang.Math.log10;

/**
 * Utility to convert between:
 * - SysBrightness, i.e., screen brightness as defined by android API 
 *   android.provider.Settings.System.SCREEN_BRIGHTNESS
 * - UI Percentage as represented by system default screen brightness slider UI
 * 
 * For some devices, e.g., ASUS, the relationship between the two is not linear.
 * In techical terms,
 * - SysBrightness is `relative luminance` (0-100%) expressed as between 0 - 255
 * - UI precerntage is `lightness`, or `value`, the percieved brightness
 * 
 * E.g., when UI slider is at half-way (50%), system brightness *is not* 127. 
 * It is about 48 (19% relative luminance).
 * 
 * @see https://en.wikipedia.org/wiki/Lightness for details of the relationship 
 *  between the two concepts.
 */
public class SysBrightnessToUISliderPctConverterRelLuminanceImpl extends SysBrightnessToUISliderPctConverter {
    // Current implmentation uses 
    // Moon and Spencer (1943) formula described in the wiki
    //   `V = 1.4 * Y ^ 0.426`
    // (The implementation here uses 0.4269)
    // Primary source:
    //   Moon, Parry; Spencer, Domina Eberle (May 1943). "Metric based 
    //   on the composite color stimulus"  
    //   JOSA. 33 (5): 270–277. doi:10.1364/JOSA.33.000270
    //   https://www.osapublishing.org/josa/abstract.cfm?uri=josa-33-5-270

    public SysBrightnessToUISliderPctConverterRelLuminanceImpl() {}

    private static final double Y_TO_BRIGHTNESS_RATIO = 255d / 100d;

    protected int doUiPctToSysBrightness(int uiPct) {
        // convert from percentage (0-100) to V (in a 0-10) scale
        final double ligthnessV = uiPct / 10d;

        // The inverse of the formula to calculate V
        final double relLuminanceYInPct = pow(10, log10(ligthnessV / 1.4) / 0.4269);

        final int sysBrightness = (int)round(relLuminanceYInPct * Y_TO_BRIGHTNESS_RATIO);
 
        return sysBrightness;
    }
    protected int doSysBrightnessToUiPct(int sysBrightness) {
        final double relLuminanceYInPct = sysBrightness / Y_TO_BRIGHTNESS_RATIO;

        // Follow the formula 
        final double lightnessV = pow(relLuminanceYInPct, 0.4269) * 1.4;

        // convert V to 0-100 percentage
        final int uiPct = (int)round(lightnessV * 10d);

        return uiPct;
    }
  
}
