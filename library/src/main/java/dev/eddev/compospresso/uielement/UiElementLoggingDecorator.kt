package dev.eddev.compospresso.uielement

import android.graphics.Point
import androidx.test.espresso.ViewAction
import dev.eddev.compospresso.interaction.Direction
import dev.eddev.compospresso.internal.CompospressoError
import dev.eddev.compospresso.internal.CompospressoLog

class UiElementLoggingDecorator(
    private val delegate: UiElement,
    private val elementName: String
) : UiElement {

    override fun tap(timeOut: Long) {
        CompospressoLog.i(elementName, "tap")
        try {
            delegate.tap(timeOut)
        } catch (e: androidx.test.espresso.NoMatchingViewException) {
            throw Error("Element [$elementName] failed to be clicked with NoMatchingViewException")
        }
    }

    override fun longTap() {
        CompospressoLog.i(elementName, "long tap")
        delegate.longTap()
    }

    override fun doubleTap() {
        CompospressoLog.i(elementName, "double tap")
        delegate.doubleTap()
    }

    override fun putText(
        text: String,
        timeOut: Long
    ) {
        CompospressoLog.i(elementName, "put text: $text")
        delegate.putText(text, timeOut)
    }

    override fun clearTextInput() {
        CompospressoLog.i(elementName, "clear text input")
        delegate.clearTextInput()
    }

    override fun setDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        CompospressoLog.i(elementName, "set date: $year-$month-$day")
        delegate.setDate(year, month, day)
    }

    override fun swipe(direction: Direction) {
        CompospressoLog.i(elementName, "swipe ${direction.name}")
        delegate.swipe(direction)
    }

    override fun pinchIn(params: Map<String, Int>?) {
        CompospressoLog.i(elementName, "pinch in")
        delegate.pinchIn(params)
    }

    override fun pinchOut(params: Map<String, Int>?) {
        CompospressoLog.i(elementName, "pinch out")
        delegate.pinchOut(params)
    }

    override fun perform(action: ViewAction) {
        CompospressoLog.i(elementName, "perform: ${action.description}")
        delegate.perform(action)
    }

    override fun isDisplayed(): Boolean {
        return delegate.isDisplayed().also {
            CompospressoLog.i(elementName, "${if (it) "" else "not "}displayed")
        }
    }

    fun isDisplayedNoLog(): Boolean {
        return delegate.isDisplayed()
    }

    override fun isEnabled(): Boolean {
        return delegate.isEnabled().also {
            CompospressoLog.i(elementName, "is ${if (it) "" else "not "}enabled")
        }
    }

    override fun isChecked(): Boolean {
        return delegate.isChecked().also {
            CompospressoLog.i(elementName, "is ${if (it) "" else "not "}checked")
        }
    }

    override fun getText(): String {
        return delegate.getText().also {
            CompospressoLog.i(elementName, "get text: $it")
        }
    }

    override fun waitDisplayed(timeOut: Long): Boolean {
        return delegate.waitDisplayed(timeOut).also {
            CompospressoLog.i(elementName, "waited displayed for $timeOut millis, ${if (it) "" else "not "}displayed")
        }
    }

    override fun waitNotDisplayed(timeOut: Long): Boolean {
        return delegate.waitNotDisplayed(timeOut).also {
            CompospressoLog.i(elementName, "wait not displayed for $timeOut millis, result: $it")
        }
    }

    override fun getPoint(): Point {
        return delegate.getPoint().also {
            CompospressoLog.i(elementName, "got point: $it")
        }
    }
}
