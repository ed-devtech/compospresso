package dev.eddev.compospresso.interaction

import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ScrollToAction
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anyOf

private const val TAG = "PerformAction"

fun Matcher<View>.performAction(action: ViewAction) {
    performActionOnView(this, action)
}

/**
 * Performs the [action] on a view described by [viewMatcher]
 *
 * Attempts to perform it using multiple scenarios for the [viewMatcher]:
 * 1. One or more views match the [viewMatcher] but only one is currently displayed.
 * 2. Only one view matches the [viewMatcher] but needs to scroll first to display it.
 * 3. Multiple views matches the [viewMatcher] and need to scroll first, but only one scrollable view is displayed.
 */
@Suppress("TooGenericExceptionCaught", "SwallowedException", "TooGenericExceptionThrown")
private fun performActionOnView(viewMatcher: Matcher<View>, action: ViewAction) {
    try {
        performActionOnDisplayedView(viewMatcher, action)
    } catch (e1: RuntimeException) {
        try {
            Log.w(TAG, "Exception while performActionOnDisplayedView()")
            scrollAndPerformAction(viewMatcher, action)
        } catch (e2: RuntimeException) {
            Log.w(TAG, "Exception while scrollAndPerformAction")
            scrollAndPerformOnDisplayedParentView(viewMatcher, action)
        } catch (finalError: RuntimeException) {
            Log.e(TAG, "performActionOnView() failed")
            throw RuntimeException("performActionOnView() failed")
        }
    }
}

private fun performActionOnDisplayedView(viewMatcher: Matcher<View>, action: ViewAction) {
    onView(
        allOf(isDisplayed(), viewMatcher)
    ).perform(action)
}

private fun scrollAndPerformAction(viewMatcher: Matcher<View>, action: ViewAction) {
    onView(viewMatcher).perform(NestedEnabledScrollToAction.nestedScrollToAction(), action)
}

private fun scrollAndPerformOnDisplayedParentView(viewMatcher: Matcher<View>, action: ViewAction) {
    onView(
        allOf(
            viewMatcher,
            ViewMatchers.isDescendantOfA(
                allOf(
                    isDisplayed(),
                    anyOf(
                        isAssignableFrom(ScrollView::class.java),
                        isAssignableFrom(HorizontalScrollView::class.java),
                        isAssignableFrom(AbsListView::class.java),
                        isAssignableFrom(NestedScrollView::class.java)
                    )
                )
            )
        )
    ).perform(scrollTo(), action)
}

internal class NestedEnabledScrollToAction private constructor() : ViewAction {

    private val scrollToAction: ScrollToAction = ScrollToAction()

    override fun getConstraints(): Matcher<View> {
        return allOf(
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
            ViewMatchers.isDescendantOfA(
                anyOf(
                    isAssignableFrom(ScrollView::class.java),
                    isAssignableFrom(HorizontalScrollView::class.java),
                    isAssignableFrom(NestedScrollView::class.java)
                )
            )
        )
    }

    override fun perform(uiController: UiController, view: View) {
        scrollToAction.perform(uiController, view)
    }

    override fun getDescription(): String {
        return scrollToAction.description
    }

    companion object {
        fun nestedScrollToAction(): NestedEnabledScrollToAction {
            return NestedEnabledScrollToAction()
        }
    }
}
