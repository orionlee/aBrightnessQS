package net.oldev.aBrightnessQS;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

public class BrightnessSettingsModelTest {

    @RunWith(Parameterized.class)
    public static class SettingsStrToArrayPosTest {
        @Parameters(name = "{index}: settingsStrToArray({0})={1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] { 
                    { "10,49,100,999", new int[]{10, 49, 100, 999} }, 
                    { "9", new int[]{9} },
                    { " 5  ,  49 , 999 ", new int[]{5, 49, 999} } 
            });
        }

        @Parameter
        public String settingsStr;

        @Parameter(1)
        public int[] settingsArrayExpected;


        @Test
        public void toArrayTest() throws Exception {
            final int[] settingsArrayActual = 
                BrightnessSettingsModel.settingsStrToArray(settingsStr);
            assertArrayEquals("Brightness Level Settings str to array conversion:", settingsArrayExpected, settingsArrayActual);
        }
    }

    @RunWith(Parameterized.class)
    public static class SettingsStrToArrayNegTest {

        @Parameters(name = "{index}: settingsStr={0}")
        public static Object[] data() {
            return new Object[] { "", "abc", "10,abc,100", "0", "0,10", "10,101", "1.5,10", null };
        }
        
        @Parameter
        public String settingsStr;

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void test() throws Exception {
            thrown.expect(IllegalArgumentException.class);
            int[] settingsArray = BrightnessSettingsModel.settingsStrToArray(settingsStr);
        }
    }
    
}
