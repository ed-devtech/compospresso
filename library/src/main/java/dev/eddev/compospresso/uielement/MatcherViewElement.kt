package dev.eddev.compospresso.uielement

import android.annotation.SuppressLint
import android.graphics.Point
import android.view.View
import android.widget.Checkable
import androidx.appcompat.widget.SwitchCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.openLinkWithText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import dev.eddev.compospresso.interaction.Direction
import dev.eddev.compospresso.interaction.clickOn
import dev.eddev.compospresso.interaction.longClickOn
import dev.eddev.compospresso.interaction.performAction
import dev.eddev.compospresso.interaction.setDatePickerDate
import dev.eddev.compospresso.interaction.typeTo
import dev.eddev.compospresso.interaction.waitForVisibility
import dev.eddev.compospresso.interaction.waitUntilGoneAction
import dev.eddev.compospresso.matcher.getText
import dev.eddev.compospresso.matcher.withCustomConstraints
import dev.eddev.compospresso.uielement.matcher_view.MatcherView
import dev.eddev.compospresso.internal.TAG
import dev.eddev.compospresso.internal.CompospressoError
import junit.framework.AssertionFailedError
import kotlin.reflect.KClass
import kotlin.reflect.cast
import org.hamcrest.Matcher

class MatcherViewElement(val matcher: Matcher<View>) : UiElement {

    constructor(matcherView: MatcherView) : this(matcherView.toMatcher())
    constructor(text: String) : this(withText(text))

    override fun tap(timeOut: Long) {
        waitDisplayed(timeOut)
        clickOn(matcher)
    }

    override fun longTap() {
        longClickOn(matcher)
    }

    override fun doubleTap() {
        matcher.performAction(ViewActions.doubleClick())
    }

    override fun putText(
        text: String,
        timeOut: Long
    ) {
        waitDisplayed(timeOut)
        typeTo(matcher, text)
    }

    @SuppressLint("RestrictedApi")
    override fun clearTextInput() {
        tap()
        val view = getView()
        if (view is android.widget.EditText) {
            runOnUiThread {
                view.text.clear()
            }
        } else {
            throw CompospressoError(TAG, "View is not an EditText")
        }
    }

    fun clickTextOnView(text: String) {
        onView(matcher).perform(openLinkWithText(text))
    }

    fun <T : SwitchCompat> setSwitch(
        checked: Boolean,
        clazz: KClass<T>
    ) {
        val switch = clazz.cast(getView())
        if (switch.isChecked != checked) {
            tap()
        }
    }

    override fun setDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        setDatePickerDate(year, month, day)
    }

    override fun swipe(direction: Direction) {
        val swipeAction = when (direction) {
            Direction.UP -> ViewActions.swipeUp()
            Direction.DOWN -> ViewActions.swipeDown()
            Direction.LEFT -> ViewActions.swipeLeft()
            Direction.RIGHT -> ViewActions.swipeRight()
        }
        onView(matcher).perform(withCustomConstraints(swipeAction, isDisplayingAtLeast(30)))
    }

    override fun pinchIn(params: Map<String, Int>?) {
        onView(matcher).perform(dev.eddev.compospresso.interaction.pinchIn())
    }

    override fun pinchOut(params: Map<String, Int>?) {
        onView(matcher).perform(dev.eddev.compospresso.interaction.pinchOut())
    }

    override fun perform(action: ViewAction) {
        onView(matcher).perform(action)
    }

    override fun isDisplayed(): Boolean {
        return try {
            onView(matcher).check(matches(ViewMatchers.isDisplayed()))
            true
        } catch (e: Throwable) {
            return false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            onView(matcher).check(matches(ViewMatchers.isEnabled()))
            true
        } catch (e: AssertionFailedError) {
            false
        } catch (e: PerformException) {
            false
        } catch (e: NoMatchingViewException) {
            false
        }
    }

    override fun isChecked(): Boolean {
        val view = this.getView() as Checkable
        return view.isChecked
    }

    override fun getText(): String {
        return onView(matcher).getText()
    }

    override fun waitDisplayed(timeOut: Long): Boolean {
        return try {
            waitForVisibility(matcher, timeOut)
            true
        } catch (_: PerformException) {
            false
        }
    }

    override fun waitNotDisplayed(timeOut: Long): Boolean {
        return try {
            onView(matcher).perform(waitUntilGoneAction(timeOut))
            true
        } catch (_: PerformException) {
            false
        }
    }

    fun getView(): View {
        var result: View? = null
        onView(matcher).check { view, _ -> result = view }
        return result ?: throw CompospressoError(TAG, "View not found")
    }

    override fun getPoint(): Point {
        val location = IntArray(2)
        this.getView().getLocationOnScreen(location)
        return Point(location[0], location[1])
    }
}

fun MatcherView.toElement(): MatcherViewElement {
    return MatcherViewElement(toMatcher())
}
