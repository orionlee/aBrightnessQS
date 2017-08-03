package net.oldev.aBrightnessQS;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * A sanity check test for Main UI
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        // Click to get dialog to update brightness levels
        ViewInteraction brightnessPctsLayout = onView(
                allOf(withId(R.id.brightnessPctsSection), isDisplayed()));
        brightnessPctsLayout.perform(click());

        // randomize levels to be entered so that the new one is unlikely to be the same as the existing one.
        final int startLevel = (int)(System.currentTimeMillis() % 50) + 1;
        String newLevelsStr = startLevel + "," + (startLevel+10) + "," + (startLevel+30);

        replaceTextInDialog(newLevelsStr);

        // Assert the new levels are now displayed.
        onView(withId(R.id.brightnessPctsOutput))
                .check(matches(withText(newLevelsStr)));

        /// delay(1000); // for visual inspection
    }

    private void replaceTextInDialog(String newText) {
        // TODO: give the EditText an ID to match
        ViewInteraction editText = onView(
                allOf(withId(987654), /// TODO: use ID, old: withText("10,30,50,75,100"),
                        withParent(allOf(withId(android.R.id.custom),
                                withParent(withClassName(is("android.widget.FrameLayout"))))),
                        isDisplayed()));
        editText.perform(click(),
                replaceText(newText),
                closeSoftKeyboard());

        // TODO: give button an ID?
        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton.perform(scrollTo(), click());
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
