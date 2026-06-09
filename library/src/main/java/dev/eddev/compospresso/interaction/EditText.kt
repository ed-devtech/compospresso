package dev.eddev.compospresso.interaction

import android.view.View
import android.widget.EditText
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anyOf

/**
 * This uses [androidx.test.espresso.action.ViewActions.typeText], to
 * tap once on the EditText and type the characters like an on-screen keyboard.
 * Most EditText properties, like `maxLength` and `textFilter` will be honored.
 */
fun typeTo(
    editTextId: Int,
    text: String
) {
    typeTo(withId(editTextId), text)
}

fun typeTo(
    matcherEditText: Matcher<View>,
    text: String
) {
    val assignableFrom = isAssignableFrom(EditText::class.java)
    val simpleMatcher = allOf(matcherEditText, assignableFrom)
    val wrapperMatcher = allOf(ViewMatchers.isDescendantOfA(matcherEditText), assignableFrom)
    val combinedMatcher = anyOf(simpleMatcher, wrapperMatcher)
    combinedMatcher.performAction(typeText(text))
}

/**
 * This uses [androidx.test.espresso.action.ViewActions.replaceText],
 * to remove any existing text and insert the characters directly,
 * ignoring any EditText properties like `maxLength`, `textFilter`, etc.
 * Super fast
 */
internal fun writeWithWaitTo(
    editTextId: Int,
    text: String
) {
    writeWithWaitTo(withId(editTextId), text)
}

internal fun writeWithWaitTo(
    matcherEditText: Matcher<View>,
    text: String
) {
    val assignableFrom = isAssignableFrom(EditText::class.java)
    val simpleMatcher = allOf(matcherEditText, assignableFrom)
    val wrapperMatcher = allOf(ViewMatchers.isDescendantOfA(matcherEditText), assignableFrom)
    val combinedMatcher = anyOf(simpleMatcher, wrapperMatcher)
    combinedMatcher.performAction(replaceText(text))
}

internal fun writeWithWaitTo(
    matcherEditText: Matcher<View>,
    text: String,
    timeOut: Long
) {
    waitForVisibility(matcherEditText, timeOut)
    writeWithWaitTo(matcherEditText, text)
}

/**
 * Clears text on the view. See also
 * [androidx.test.espresso.action.ViewActions.clearText].
 */
internal fun clearText(editTextId: Int) {
    withId(editTextId).performAction(clearText())
}
