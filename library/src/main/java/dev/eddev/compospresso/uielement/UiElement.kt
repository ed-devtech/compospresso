package dev.eddev.compospresso.uielement

import android.graphics.Point
import android.view.View
import androidx.test.espresso.ViewAction
import androidx.test.uiautomator.UiSelector
import dev.eddev.compospresso.interaction.Direction
import dev.eddev.compospresso.matcher.resourceMatcher
import dev.eddev.compospresso.uielement.compose.ComposeMatcher
import dev.eddev.compospresso.uielement.matcher_view.MatcherView
import dev.eddev.compospresso.uielement.matcher_view.MatcherViewBuilder
import java.lang.Thread.sleep
import org.hamcrest.Matcher

sealed interface UiElement {
    fun tap(timeOut: Long = 0L)
    fun longTap()
    fun doubleTap()
    fun putText(
        text: String,
        timeOut: Long = 0L
    )

    fun clearTextInput()

    fun setDate(
        year: Int,
        month: Int,
        day: Int
    )

    fun swipe(direction: Direction)

    /**
     * Zoom out
     */
    fun pinchIn(params: Map<String, Int>? = null)

    /**
     * Zoom in
     */
    fun pinchOut(params: Map<String, Int>? = null)
    fun perform(action: ViewAction)

    fun isDisplayed(): Boolean
    fun isEnabled(): Boolean
    fun isChecked(): Boolean
    fun getText(): String
    fun waitDisplayed(timeOut: Long): Boolean
    fun waitNotDisplayed(timeOut: Long): Boolean
    fun waitContains(
        expectedSubstring: String,
        timeout: Long,
        contains: Boolean,
        pullingInterval: Long = 250L,
    ): Boolean {
        val endTime = System.currentTimeMillis() + timeout
        while (System.currentTimeMillis() < endTime) {
            if (getText().contains(expectedSubstring) == contains) {
                return true
            }
            sleep(pullingInterval)
        }
        return false
    }

    fun getPoint(): Point
}

// --- Espresso ---
@Suppress("FunctionName")
fun UiElement(vararg resIds: Int) = MatcherViewElement(resIds.resourceMatcher())

@Suppress("FunctionName")
fun UiElement(
    resId: Int,
    text: String
) = MatcherViewElement(MatcherView(resId, text))

@Suppress("FunctionName")
fun UiElement(matcher: Matcher<View>) = MatcherViewElement(matcher)

@Suppress("FunctionName")
fun UiElement(matcherView: MatcherView) = MatcherViewElement(matcherView)

@Suppress("FunctionName")
fun UiElement(builder: MatcherViewBuilder) = MatcherViewElement(builder.buildMatcher())
// -- end of: Espresso region --

@Suppress("FunctionName")
fun UiElement(uiSelector: UiSelector) = UiSelectorElement(uiSelector)

@Suppress("FunctionName")
fun UiElement(composeMatcher: ComposeMatcher) = ComposeElement(composeMatcher)

fun UiElement.withLogging(elementName: String): UiElement {
    return UiElementLoggingDecorator(this, elementName)
}
