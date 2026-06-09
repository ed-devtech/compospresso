package dev.eddev.compospresso.matcher

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun <T> atPosition(
    position: Int,
    matcher: Matcher<T>
): Matcher<T> {
    return object : BaseMatcher<T>() {
        var matchingPosition = 0
        override fun matches(item: Any): Boolean {
            if (!matcher.matches(item)) {
                return false
            }
            return matchingPosition++ == position
        }

        override fun describeTo(description: Description) {
            description.appendText("should return matching item at position $position")
        }
    }
}

fun <T> firstViewOf(matcher: Matcher<T>): Matcher<T> {
    return object : BaseMatcher<T>() {
        private var isFirst = true
        override fun matches(item: Any): Boolean {
            if (isFirst && matcher.matches(item)) {
                isFirst = false
                return true
            }
            return false
        }

        override fun describeTo(description: Description) {
            description.appendText("should return first matching item")
        }
    }
}

fun nthChildOf(
    parentMatcher: Matcher<View>,
    childPosition: Int
): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("with $childPosition child view of type parentMatcher")
        }

        public override fun matchesSafely(view: View): Boolean {
            if (view.parent !is ViewGroup) return false
            val parent = view.parent as ViewGroup
            return parentMatcher.matches(parent) && parent.getChildAt(childPosition) == view
        }
    }
}

fun withCustomConstraints(
    action: ViewAction,
    constraints: Matcher<View>
): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return constraints
        }

        override fun getDescription(): String {
            return action.description
        }

        override fun perform(
            uiController: UiController,
            view: View
        ) {
            action.perform(uiController, view)
        }
    }
}
