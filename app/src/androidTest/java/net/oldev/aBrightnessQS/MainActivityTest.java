package net.oldev.aBrightnessQS;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * A sanity check test for Main UI
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SmallTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final boolean RELAUNCH_ACTIVITY_TRUE = true;
    private static final int USE_IME_ACTION_BUTTON = -1;

    // Note: The test relies on the fact the activity is relaunched per test method,
    // so that we can test settings persistence.
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, RELAUNCH_ACTIVITY_TRUE);

    // Used to hold the new levels from test t1 to t2
    private static String msNewLevelsStr;

    @Test
    public void t1ModifySettings() {
        // Click to get dialog to update brightness levels
        ViewInteraction brightnessPctsLayout = onView(
                allOf(withId(R.id.brightnessPctsSection), isDisplayed()));
        brightnessPctsLayout.perform(click());

        // randomize levels to be entered so that the new one is unlikely to be the same as the existing one.
        final int startLevel = (int)(System.currentTimeMillis() % 50) + 1;
        msNewLevelsStr = startLevel + "," + (startLevel+10) + "," + (startLevel+30);

        replaceTextInDialog(R.id.brightnessPctsInput, R.string.ok_btn_label, msNewLevelsStr);

        // Assert the new levels are now displayed.
        onView(withId(R.id.brightnessPctsOutput))
                .check(matches(withText(msNewLevelsStr)));

        /// delay(1000); // for visual inspection

    }

    @Test
    public void t2SettingsPersisted() {
        // Re-assert the new levels have been persisted, and are now displayed.
        // upon activity relaunch.
        checkSettingsOutput((msNewLevelsStr));
    }

    @Test
    public void t3ModifySettingsNegative() {
        // Click to get dialog to update brightness levels
        ViewInteraction brightnessPctsLayout = onView(
                allOf(withId(R.id.brightnessPctsSection), isDisplayed()));
        brightnessPctsLayout.perform(click());

        // randomize levels to be entered so that the new one is unlikely to be the same as the existing one.
        final int startLevel = (int)(System.currentTimeMillis() % 50) + 1;
        String negNewLevelsStr = "1,19,101"; // so that it is not a valid string

        replaceTextInDialog(R.id.brightnessPctsInput, R.string.ok_btn_label, negNewLevelsStr);

        // Assert that alert dialog is still displayed (with the invalid settings in question)
        onView(withId(R.id.brightnessPctsInput))
                .check(matches(withText(negNewLevelsStr)));

    }

    @Test
    public void t4NegSettingsNotPersisted() {
        // Re-assert the existing levels remain there. The invalid settings have not been persisted.
        // upon activity relaunch.
        checkSettingsOutput((msNewLevelsStr));
    }

    /**
     * Similar to #t1ModifySettings, except using IME button instead of dialog ok button.
     */
    @Test
    public void t5ModifySettingsWithImeActionButton() {
        // Click to get dialog to update brightness levels
        ViewInteraction brightnessPctsLayout = onView(
                allOf(withId(R.id.brightnessPctsSection), isDisplayed()));
        brightnessPctsLayout.perform(click());

        // randomize levels to be entered so that the new one is unlikely to be the same as the existing one.
        final int startLevel = (int)(System.currentTimeMillis() % 50) + 1;
        msNewLevelsStr = startLevel + "," + (startLevel+10) + "," + (startLevel+30);

        replaceTextInDialog(R.id.brightnessPctsInput, USE_IME_ACTION_BUTTON, msNewLevelsStr);

        // Assert the new levels are now displayed.
        onView(withId(R.id.brightnessPctsOutput))
                .check(matches(withText(msNewLevelsStr)));
    }

    @Test
    public void t6SettingsPersisted() {
        checkSettingsOutput((msNewLevelsStr));
    }


    /**
     * Enter (& replace) text in a dialog, e.g., those generated by AlertDialog.builder
     *
     * @param dialogEditTextId the id of the EditText view within the dialog
     * @param dialogPositiveButtonLabelId the id of the *label* of the positive button, -1 to use IME Action button instead (Ideally id of the button would be used, but it could not be set)
     * @param newText the new text to be entered.
     *
     * @see ViewActions#pressImeActionButton()
     */
    private void replaceTextInDialog(int dialogEditTextId, int dialogPositiveButtonLabelId, String newText) {
        ViewInteraction editText = onView(
                allOf(withId(dialogEditTextId),
                        // withParent(...) check obtained from espresso recorder
                        withParent(allOf(withId(R.id.custom),
                                withParent(withId(R.id.customPanel)))),
                        isDisplayed()));
        editText.perform(click(),
                replaceText(newText));

        if (dialogPositiveButtonLabelId < 0) {
            editText.perform(pressImeActionButton());
        } else {
            editText.perform(closeSoftKeyboard());

            ViewInteraction positiveButton = onView(
                    allOf(withText(dialogPositiveButtonLabelId),
                            isAssignableFrom(android.widget.Button.class),
                            isDisplayed()));
            positiveButton.perform(scrollTo(), click());
        }
    }

    private void checkSettingsOutput(String expected) {
        onView(withId(R.id.brightnessPctsOutput))
                .check(matches(withText(expected)));
    }


    private static void delay(long delayMillis) {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
