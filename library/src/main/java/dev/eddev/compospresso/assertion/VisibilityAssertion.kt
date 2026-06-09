package dev.eddev.compospresso.assertion

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import dev.eddev.compospresso.matcher.resourceMatcher
import dev.eddev.compospresso.matcher.withCompatText
import dev.eddev.compospresso.uielement.UiElement
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Assert.assertTrue

fun assertDisplayed(viewMatcher: Matcher<View>) {
    viewMatcher.assertAny(isDisplayed())
}

fun assertDisplayed(viewId: Int) {
    viewId.resourceMatcher().assertAny(isDisplayed())
}

fun assertDisplayed(text: String) {
    withCompatText(text).assertAny(isDisplayed())
}

fun assertDisplayed(
    @IdRes viewId: Int,
    text: String
) {
    viewId.resourceMatcher().assertAny(
        allOf(isDisplayed(), withCompatText(text))
    )
}

fun assertDisplayed(
    @IdRes viewId: Int,
    @StringRes stringId: Int
) {
    viewId.resourceMatcher().assertAny(
        allOf(isDisplayed(), withText(stringId))
    )
}

private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

fun assertDisplayed(uiElement: UiElement) {
    assertTrue(uiElement.isDisplayed())
}

/**
 * Assert not displayed
 * If doesn't exist will throw NoMatchingViewException exception.
 */
fun assertNotDisplayed(resId: Int) {
    resId.resourceMatcher().assertAny(not(isDisplayed()))
}

fun assertNotDisplayed(text: String) {
    withCompatText(text).assertAny(not(isDisplayed()))
}

fun assertNotDisplayed(viewMatcher: Matcher<View>) {
    viewMatcher.assertAny(not(isDisplayed()))
}

fun assertNotDisplayed(
    @IdRes viewId: Int,
    text: String
) {
    viewId.resourceMatcher().assertAny(
        not(allOf(isDisplayed(), withCompatText(text)))
    )
}

fun assertNotDisplayed(
    @IdRes viewId: Int,
    @StringRes stringId: Int
) {
    viewId.resourceMatcher().assertAny(
        not(allOf(isDisplayed(), withText(stringId)))
    )
}
