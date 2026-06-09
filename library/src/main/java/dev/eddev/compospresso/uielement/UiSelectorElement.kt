package dev.eddev.compospresso.uielement

import android.graphics.Point
import androidx.test.espresso.ViewAction
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dev.eddev.compospresso.interaction.Direction
import dev.eddev.compospresso.internal.TAG
import dev.eddev.compospresso.internal.CompospressoError

class UiSelectorElement(private val uiSelector: UiSelector) : UiElement {
    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    override fun tap(timeOut: Long) {
        waitDisplayed(timeOut)
        device.findObject(uiSelector).click()
    }

    override fun longTap() {
        device.findObject(uiSelector).longClick()
    }

    override fun doubleTap() {
        throw CompospressoError(TAG, "UiSelector does not support double click, use MatcherView instead or replace with repeated click()")
    }

    override fun putText(
        text: String,
        timeOut: Long
    ) {
        waitDisplayed(timeOut)
        device.findObject(uiSelector).setText(text)
    }

    override fun clearTextInput() {
        device.findObject(uiSelector).clearTextField()
    }

    fun setSwitch(checked: Boolean) {
        if (isChecked() != checked) {
            tap()
        }
    }

    override fun setDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        throw CompospressoError(TAG, "UiSelector does not support setting date, use MatcherView instead")
    }

    override fun swipe(direction: Direction) {
        throw CompospressoError(TAG, "UiSelector does not support swiping, use MatcherView instead")
    }

    override fun pinchIn(params: Map<String, Int>?) {
        val percent = checkNotNull(params?.get("percent")).toInt()
        val steps = checkNotNull(params?.get("steps")).toInt()
        device.findObject(uiSelector).pinchIn(percent, steps)
    }

    override fun pinchOut(params: Map<String, Int>?) {
        val percent = checkNotNull(params?.get("percent")).toInt()
        val steps = checkNotNull(params?.get("steps")).toInt()
        device.findObject(uiSelector).pinchOut(percent, steps)
    }

    override fun perform(action: ViewAction) {
        throw CompospressoError(TAG, "UiSelector does not support performing actions, use MatcherView instead")
    }

    override fun isDisplayed(): Boolean {
        return device.findObject(uiSelector).exists()
    }

    override fun isEnabled(): Boolean {
        return device.findObject(uiSelector).isEnabled
    }

    override fun isChecked(): Boolean {
        return device.findObject(uiSelector).isChecked
    }

    override fun getText(): String {
        return device.findObject(uiSelector).getText()
    }

    override fun waitDisplayed(timeOut: Long): Boolean {
        return device.findObject(uiSelector).waitUntilGone(timeOut)
    }

    override fun waitNotDisplayed(timeOut: Long): Boolean {
        return device.findObject(uiSelector).waitUntilGone(timeOut)
    }

    override fun getPoint(): Point {
        val location = IntArray(2)
        device.findObject(uiSelector).visibleBounds.let {
            location[0] = it.centerX()
            location[1] = it.centerY()
        }
        return Point(location[0], location[1])
    }
}
