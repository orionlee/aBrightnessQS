package net.oldev.aBrightnessQS;

import static java.lang.Math.round;

/**
 * Conversion between system brightness to UI slider percentage 
 * for devices of which the relationship is simple linear one.
 */
public class SysBrightnessToUISliderPctConverterLinearImpl extends SysBrightnessToUISliderPctConverter {

    public SysBrightnessToUISliderPctConverterLinearImpl() {}


    protected int doUiPctToSysBrightness(int uiPct) {
        final int sysBrightness = (int)round(uiPct * 255d / 100d);
 
        return sysBrightness;
    }
    protected int doSysBrightnessToUiPct(int sysBrightness) {
        final int uiPct = (int)round(sysBrightness / 255d * 100d);

        return uiPct;
    }
  
}