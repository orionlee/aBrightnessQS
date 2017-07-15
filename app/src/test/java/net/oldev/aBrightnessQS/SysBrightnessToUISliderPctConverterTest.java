package net.oldev.aBrightnessQS;

import org.junit.Test;

import static org.junit.Assert.*;
import static net.oldev.aBrightnessQS.SysBrightnessToUISliderPctConverter.*;

public class SysBrightnessToUISliderPctConverterTest {

    public static class PositiveTest {

        // Test driver for positive tests,
        // starting with uiPct (0 - 100)
        private void tWithUiPct(int uiPctInTest, int sysBrightnessExpected) throws Exception {
            final int sysBrightnessActual = uiPctToSysBrightness(uiPctInTest);
            assertEquals("uiPct to sysBrightness conversion:",  sysBrightnessExpected, sysBrightnessActual);

            // Do reverse conversion test
            //  The exepected result should be the uiPct supplied by the caller
            final int uiPctExpected = uiPctInTest; 
            final int uiPctConverted = sysBrightnessToUiPct(sysBrightnessActual);

            assertEquals("sysBrightness to uiPct conversion:", uiPctExpected, uiPctConverted);

        }

        @Test
        public void uiAt0() throws Exception {
            tWithUiPct(0, 0);
        }

        @Test
        public void uiAt20() throws Exception {
            tWithUiPct(20, 128);
        }

        @Test
        public void uiAt25() throws Exception {
            tWithUiPct(25, 141);
        }
        @Test
        public void uiAt50() throws Exception {
            tWithUiPct(50, 190);
        }

        @Test
        public void uiAt75() throws Exception {
            tWithUiPct(75, 225);
        }

        @Test
        public void uiAt100() throws Exception {
            tWithUiPct(100, 255);
        }
    }
  
    public static class NegativeTest {

        private void negt4UI(int uiPct) throws Exception {
            try {
                int sysBrightnessActual = uiPctToSysBrightness(uiPct);
                fail("Test for illegal uiPct failed. uiPct for test:<" + uiPct + ">.  expected:<IllegalArgumentException> but was:<" +  sysBrightnessActual +">");
            } catch (IllegalArgumentException iae) {
            }
        }

        private void negt4SysBrightness(int sysBrightness) throws Exception {
            try {
                int uiPctActual = sysBrightnessToUiPct(sysBrightness);
                fail("Test for illegal sysBrightness failed. sysBrightness for test:<" + sysBrightness + ">.  expected:<IllegalArgumentException> but was:<" +  sysBrightness +">");
            } catch (IllegalArgumentException iae) {
            }
        }

        @Test
        public void negUIAtNeg() throws Exception {
            negt4UI(-1);
        }

        @Test
        public void negUIAt101() throws Exception {
            negt4UI(101);
        }

        @Test
        public void negSysBrightnessAtNeg() throws Exception {
            negt4SysBrightness(-1);
        }

        @Test
        public void negSysBrightnessAt256() throws Exception {
            negt4SysBrightness(256);
        }
    }

}