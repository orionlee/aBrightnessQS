package net.oldev.aBrightnessQS;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;
import static net.oldev.aBrightnessQS.SysBrightnessToUISliderPctConverter.*;

public class SysBrightnessToUISliderPctConverterTest {

    @RunWith(Parameterized.class)
    public static class PositiveTest {

        @Parameters(name = "{index}: uiPctToSysBrightness({0})={1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] { 
                    { 0, 0 }, { 20, 128 }, { 25, 141 }, 
                    { 50, 190 }, { 75, 225 }, { 100, 255 }
            });
        }

        @Parameter
        public int uiPctInTest;

        @Parameter(1)
        public int sysBrightnessExpected;

        // Test driver for positive tests.
        // Given a uiPct (0 - 100), convert to sysBrightness;
        // then convert it back.
        @Test
        public void tWithUiPct() throws Exception {
            final int sysBrightnessActual = uiPctToSysBrightness(uiPctInTest);
            assertEquals("uiPct to sysBrightness conversion:",  sysBrightnessExpected, sysBrightnessActual);

            // Do reverse conversion test
            //  The exepected result should be the uiPct supplied by the caller
            final int uiPctExpected = uiPctInTest; 
            final int uiPctConverted = sysBrightnessToUiPct(sysBrightnessActual);

            assertEquals("sysBrightness to uiPct conversion:", uiPctExpected, uiPctConverted);

        }

    }
  
    public static class NegativeTest {

        private void negt4UI(int uiPct) throws Exception {
            int sysBrightnessActual = uiPctToSysBrightness(uiPct);
            /// fail("Test for illegal uiPct failed. uiPct for test:<" + uiPct + ">.  expected:<IllegalArgumentException> but was:<" +  sysBrightnessActual +">");
        }

        private void negt4SysBrightness(int sysBrightness) throws Exception {
            int uiPctActual = sysBrightnessToUiPct(sysBrightness);
            ///fail("Test for illegal sysBrightness failed. sysBrightness for test:<" + sysBrightness + ">.  expected:<IllegalArgumentException> but was:<" +  sysBrightness +">");
        }

        @Test(expected = IllegalArgumentException.class)
        public void negUIAtNeg() throws Exception {
            negt4UI(-1);
        }

        @Test(expected = IllegalArgumentException.class)
        public void negUIAt101() throws Exception {
            negt4UI(101);
        }

        @Test(expected = IllegalArgumentException.class)
        public void negSysBrightnessAtNeg() throws Exception {
            negt4SysBrightness(-1);
        }

        @Test(expected = IllegalArgumentException.class)
        public void negSysBrightnessAt256() throws Exception {
            negt4SysBrightness(256);
        }
    }

}