package net.oldev.aBrightnessQS;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

public class SysBrightnessToUISliderPctConverterTest {

    private static SysBrightnessToUISliderPctConverterI newOf(Class<? extends SysBrightnessToUISliderPctConverterI> clazz) {
        try {
            Constructor<? extends SysBrightnessToUISliderPctConverterI> cons = clazz.getConstructor();
            SysBrightnessToUISliderPctConverterI obj = cons.newInstance();
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception in creating a SysBrightnessToUISliderPctConverterI instance for test", e);
        }
    }

    // Convenience references for the implmentation calsses udner test
    private static Class<? extends SysBrightnessToUISliderPctConverterI> cRl = SysBrightnessToUISliderPctConverterRelLuminanceImpl.class;
    private static Class<? extends SysBrightnessToUISliderPctConverterI> cLi = SysBrightnessToUISliderPctConverterLinearImpl.class;            

    @RunWith(Parameterized.class)
    public static class PositiveTest {

        @Parameters(name = "{index}: <{0}> uiPctToSysBrightness({1})={2}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] { 
                    { cRl, 0, 0 }, { cRl, 10, 1 }, { cRl, 20, 6 }, { cRl, 25, 10 }, 
                    { cRl, 50, 50 }, { cRl, 75, 130 }, { cRl, 100, 255 },
                    { cLi, 0, 0 }, { cLi, 1, 3 }, { cLi, 5, 13 }, { cLi, 25, 64 }, 
                    { cLi, 50, 128 }, { cLi, 75, 191 }, { cLi, 100, 255 }
            });
        }

        @Parameter
        public Class<? extends SysBrightnessToUISliderPctConverterI> clazzToTest;

        @Parameter(1)
        public int uiPctInTest;

        @Parameter(2)
        public int sysBrightnessExpected;

        // Test driver for positive tests.
        // Given a uiPct (0 - 100), convert to sysBrightness;
        // then convert it back.
        @Test
        public void tWithUiPct() throws Exception {
            final SysBrightnessToUISliderPctConverterI c = newOf(clazzToTest);
            final int sysBrightnessActual = c.uiPctToSysBrightness(uiPctInTest);
            assertEquals("1) uiPct to sysBrightness conversion:",  sysBrightnessExpected, sysBrightnessActual);

            // Do reverse conversion test
            //  The exepected result should be the uiPct supplied by the caller
            final int uiPctExpected = uiPctInTest; 
            final int uiPctConverted = c.sysBrightnessToUiPct(sysBrightnessActual);

            // since the conversion involves rounding to integer
            // reverse the conversion could induce slight rounding error
            assertEquals("2) sysBrightness to uiPct conversion:", (float)uiPctExpected, (float)uiPctConverted, 1f);

        }

    }

    @RunWith(Parameterized.class)
    public static class Negative4UiPctTest {

        @Parameters(name = "{index}: <{0}> uiPct={1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] { 
                {cRl, -1}, {cRl, 101}, 
                {cLi, -1}, {cLi, 101} 
            });
        }
        
        @Parameter
        public Class<? extends SysBrightnessToUISliderPctConverterI> clazzToTest;

        @Parameter(1)
        public int uiPct;

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void test() throws Exception {
            thrown.expect(IllegalArgumentException.class);
            final SysBrightnessToUISliderPctConverterI c = newOf(clazzToTest);
            int sysBrightnessActual = c.uiPctToSysBrightness(uiPct);
        }
    }

    @RunWith(Parameterized.class)
    public static class Negative4SysBrightnessTest {

        @Parameters(name = "{index}: <{0}> sysBrightness={1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] { 
                {cRl, -1}, {cRl, 256},  
                {cLi, -1}, {cLi, 256}  
            });
        }

        @Parameter
        public Class<? extends SysBrightnessToUISliderPctConverterI> clazzToTest;

        @Parameter(1)
        public int sysBrightness;

        @Rule
        public ExpectedException thrown = ExpectedException.none();
        
        @Test
        public void test() throws Exception {
            thrown.expect(IllegalArgumentException.class);
            final SysBrightnessToUISliderPctConverterI c = newOf(clazzToTest);
            int uiPctActual = c.sysBrightnessToUiPct(sysBrightness);
        }
    }    
    
}