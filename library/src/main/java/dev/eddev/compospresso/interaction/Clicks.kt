package dev.eddev.compospresso.interaction

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.longClick
import dev.eddev.compospresso.matcher.resourceMatcher
import dev.eddev.compospresso.matcher.withCompatText
import org.hamcrest.Matcher

/**
 * Espresso clicks
 */

fun clickBack() {
    Espresso.pressBack()
}

fun clickOn(matcher: Matcher<View>) {
    matcher.performAction(ViewActions.click())
}

fun clickOn(resId: Int) {
    clickOn(resId.resourceMatcher())
}

fun clickOn(vararg resId: Int) {
    clickOn(resId.resourceMatcher())
}

fun clickOn(text: String) {
    clickOn(withCompatText(text))
}

fun longClickOn(matcher: Matcher<View>) {
    matcher.performAction(longClick())
}

fun longClickOn(resId: Int) {
    longClickOn(resId.resourceMatcher())
}

fun longClickOn(text: String) {
    longClickOn(withCompatText(text))
}
