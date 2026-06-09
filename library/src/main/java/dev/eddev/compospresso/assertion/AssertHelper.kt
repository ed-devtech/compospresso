package dev.eddev.compospresso.assertion

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import androidx.test.espresso.AmbiguousViewMatcherException
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.runner.intent.IntentStubberRegistry
import dev.eddev.compospresso.internal.CompospressoError
import dev.eddev.compospresso.matcher.firstViewOf
import dev.eddev.compospresso.uielement.MatcherViewElement
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not

/**
 * Extension function alias for [assertAnyView]
 */
internal fun Matcher<View>.assertAny(condition: Matcher<View>) {
    assertAnyView(viewMatcher = this, condition = condition)
}

/**
 * Performs an assertion of a [condition] on a view described by [viewMatcher].
 *
 * Attempts to assert using multiple scenarios for the [viewMatcher]:
 * 1. Just one view matches the [viewMatcher].
 * 2. Multiple views match the [viewMatcher]: will pass if at least one of them matches the [condition].
 */
@Suppress("SwallowedException")
internal fun assertAnyView(
    viewMatcher: Matcher<View>,
    condition: Matcher<View>
) {
    try {
        tryToAssert(viewMatcher, condition)
    } catch (multipleViewsError: AmbiguousViewMatcherException) {
        tryToAssertFirstView(viewMatcher, condition)
    }
}

private fun tryToAssertFirstView(
    viewMatcher: Matcher<View>,
    condition: Matcher<View>
) {
    Espresso.onView(firstViewOf(allOf(viewMatcher, condition)))
        .check(ViewAssertions.matches(condition))
}

private fun tryToAssert(
    viewMatcher: Matcher<View>,
    condition: Matcher<View>
) {
    Espresso.onView(viewMatcher).check(ViewAssertions.matches(condition))
}

fun Matcher<View>.assertEnabled() {
    assertAny(isEnabled())
}

fun Matcher<View>.assertDisabled() {
    assertAny(not(isEnabled()))
}

/**
 * Validates presence of url on a text by verifying an intent on a link click
 * If multiple links are validated one right after another put a 1 second sleep
 *
 * Call Intents.init() before calling this function
 * Call Intents.release() after calling this function if you need to proceed with other validations
 *
 * @param substringWithUrl - text, containing link
 * @param expectedUrl - expected url on the click
 */
fun MatcherViewElement.assertTextHasLink(
    substringWithUrl: String,
    expectedUrl: String,
) {
    if (!IntentStubberRegistry.isLoaded()) {
        throw CompospressoError(
            "assertTextHasLink",
            "Intents are not initialized. Call Intents.init() before calling this function"
        )
    }
    val expectedIntent =
        allOf(IntentMatchers.hasAction(Intent.ACTION_VIEW), IntentMatchers.hasData(expectedUrl))
    Intents.intending(expectedIntent)
        .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
    this.perform(ViewActions.openLinkWithText(substringWithUrl))
    Intents.intended(expectedIntent)
}

/**
 * Util to validate that click on an element causes an intent.
 * Click is intercepted and not happens on the screen
 */
fun MatcherViewElement.assertClickIntent(
    expectedIntent: Matcher<Intent>,
    respond: Instrumentation.ActivityResult = Instrumentation.ActivityResult(
        Activity.RESULT_CANCELED,
        null
    ),
) {
    Intents.init()
    Intents.intending(expectedIntent).respondWith(respond)
    this.tap()
    Intents.intended(expectedIntent)
    Intents.release()
}
