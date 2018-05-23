package com.nsa.teamtwo.welshpharmacy.ui.activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class, false, false);

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Before
    public void prepareTest() {
        //Adapted from https://stackoverflow.com/questions/37597080/reset-app-state-between-instrumentationtestcase-runs?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        //Accessed 17/05/2018
        File root = InstrumentationRegistry.getTargetContext().getFilesDir().getParentFile();
        String[] sharedPreferencesFileNames = new File(root, "shared_prefs").list();
        if (sharedPreferencesFileNames != null) {
            for (String fileName : sharedPreferencesFileNames) {
                SharedPreferences sharedPreferences = InstrumentationRegistry.getTargetContext().getSharedPreferences(fileName.replace(".xml", ""), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KeyValueHelper.KEY_SHOW_SETUP, false);
                editor.putString(KeyValueHelper.KEY_PAGE, "settings");
                editor.putString(KeyValueHelper.KEY_LANGUAGE, "en");
                editor.commit();
            }
        }
    }

    @Test
    public void settingsTest() {
        mActivityTestRule.launchActivity(null);

        ViewInteraction viewGroup = onView(
                allOf(withId(R.id.toolbar),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                        0),
                                0),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.layout_font),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.text_font_setting), withText("Font Size"),
                        childAtPosition(
                                allOf(withId(R.id.layout_font),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("Font Size")));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.button_fontsize),
                        childAtPosition(
                                allOf(withId(R.id.layout_font),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                2),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.layout_language),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        0),
                                1),
                        isDisplayed()));
        relativeLayout2.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_language_setting), withText("Language"),
                        childAtPosition(
                                allOf(withId(R.id.layout_language),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("Language")));

        ViewInteraction switch_4 = onView(
                allOf(withId(R.id.switch_language),
                        childAtPosition(
                                allOf(withId(R.id.layout_language),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                2),
                        isDisplayed()));
        switch_4.check(matches(isDisplayed()));
        switch_4.perform(click());

        ViewInteraction switch_2_Welsh = onView(
                allOf(withId(R.id.switch_language), withText("Cymraeg"),
                        childAtPosition(
                                allOf(withId(R.id.layout_language),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                2),
                        isDisplayed()));
        switch_2_Welsh.check(matches(isDisplayed()));

        ViewInteraction textLanguage = onView(
                allOf(withId(R.id.text_language_setting), withText("Iaith"),
                        childAtPosition(
                                allOf(withId(R.id.layout_language),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                1),
                        isDisplayed()));
        textLanguage.check(matches(isDisplayed()));

        switch_2_Welsh.perform(click());

        ViewInteraction relativeLayout3 = onView(
                allOf(withId(R.id.layout_permission_toggle),
                        childAtPosition(
                                allOf(withId(R.id.layout_location),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                2)),
                                0),
                        isDisplayed()));
        relativeLayout3.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.text_location), withText("Location"),
                        childAtPosition(
                                allOf(withId(R.id.layout_permission_toggle),
                                        childAtPosition(
                                                withId(R.id.layout_location),
                                                0)),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("Location")));

        ViewInteraction switch_2 = onView(
                allOf(withId(R.id.switch_location_permission),
                        childAtPosition(
                                allOf(withId(R.id.layout_permission_toggle),
                                        childAtPosition(
                                                withId(R.id.layout_location),
                                                0)),
                                2),
                        isDisplayed()));
        switch_2.check(matches(isDisplayed()));
        switch_2.check(matches(withText("Allow")));

        ViewInteraction relativeLayout4 = onView(
                allOf(withId(R.id.layout_radius),
                        childAtPosition(
                                allOf(withId(R.id.layout_location),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                2)),
                                2),
                        isDisplayed()));
        relativeLayout4.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.text_radius), withText("Select radius"),
                        childAtPosition(
                                allOf(withId(R.id.layout_radius),
                                        childAtPosition(
                                                withId(R.id.layout_location),
                                                2)),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("Select radius")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.text_min_miles), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.layout_radius),
                                        2),
                                0),
                        isDisplayed()));
        textView5.check(matches(withText("5")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.text_max_miles), withText("150"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.layout_radius),
                                        2),
                                2),
                        isDisplayed()));
        textView6.check(matches(withText("150")));

        ViewInteraction seekBar = onView(
                allOf(withId(R.id.seek_radius),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.layout_radius),
                                        2),
                                1),
                        isDisplayed()));
        seekBar.check(matches(isDisplayed()));

        ViewInteraction relativeLayout5 = onView(
                allOf(withId(R.id.layout_theme),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        0),
                                3),
                        isDisplayed()));
        relativeLayout5.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.image_theme),
                        childAtPosition(
                                allOf(withId(R.id.layout_theme),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                3)),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.text_select_theme), withText("Select Theme"),
                        childAtPosition(
                                allOf(withId(R.id.layout_theme),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                3)),
                                1),
                        isDisplayed()));
        textView7.check(matches(withText("Select Theme")));

        ViewInteraction button = onView(
                allOf(withId(R.id.button_white_theme),
                        childAtPosition(
                                allOf(withId(R.id.layout_theme),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                3)),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.button_black_theme),
                        childAtPosition(
                                allOf(withId(R.id.layout_theme),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                3)),
                                3),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.text_auto_change), withText("Change automatically"),
                        childAtPosition(
                                allOf(withId(R.id.layout_theme),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                3)),
                                4),
                        isDisplayed()));
        textView8.check(matches(withText("Change automatically")));

        ViewInteraction switch_3 = onView(
                allOf(withId(R.id.switch_auto_theme),
                        childAtPosition(
                                allOf(withId(R.id.layout_theme),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                3)),
                                5),
                        isDisplayed()));
        switch_3.check(matches(isDisplayed()));

        ViewInteraction relativeLayout6 = onView(
                allOf(withId(R.id.layout_reset),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        0),
                                4),
                        isDisplayed()));
        relativeLayout6.check(matches(isDisplayed()));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.image_reset),
                        childAtPosition(
                                allOf(withId(R.id.layout_reset),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                4)),
                                0),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.text_reset), withText("Reset App"),
                        childAtPosition(
                                allOf(withId(R.id.layout_reset),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                4)),
                                1),
                        isDisplayed()));
        textView9.check(matches(withText("Reset App")));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.button_reset),
                        childAtPosition(
                                allOf(withId(R.id.layout_reset),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                4)),
                                2),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
