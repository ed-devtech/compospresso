package dev.eddev.compospresso.uielement

import android.graphics.Point
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.ViewAction
import dev.eddev.compospresso.interaction.Direction
import dev.eddev.compospresso.internal.CompospressoError
import dev.eddev.compospresso.internal.TAG
import dev.eddev.compospresso.uielement.compose.ComposeMatcher
import dev.eddev.compospresso.uielement.compose.ComposeTestRegistry

class ComposeElement(
    private val composeMatcher: ComposeMatcher
) : UiElement {
    private fun node(): SemanticsNodeInteraction = composeMatcher.toNodeInteraction()

    override fun tap(timeOut: Long) {
        if (timeOut > 0L) waitDisplayed(timeOut)
        node().performClick()
    }

    override fun longTap() {
        node().performTouchInput { longClick() }
    }

    override fun doubleTap() {
        node().performTouchInput { doubleClick() }
    }

    override fun putText(
        text: String,
        timeOut: Long
    ) {
        if (timeOut > 0L) waitDisplayed(timeOut)
        node().performTextInput(text)
    }

    override fun clearTextInput() {
        node().performTextClearance()
    }

    override fun setDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        throw UnsupportedOperationException(
            "setDate is not supported for Compose elements; interact with the picker via semantics instead"
        )
    }

    override fun swipe(direction: Direction) {
        node().performTouchInput {
            when (direction) {
                Direction.UP -> swipeUp()
                Direction.DOWN -> swipeDown()
                Direction.LEFT -> swipeLeft()
                Direction.RIGHT -> swipeRight()
            }
        }
    }

    override fun pinchIn(params: Map<String, Int>?) {
        throw UnsupportedOperationException("pinchIn is not supported for Compose elements")
    }

    override fun pinchOut(params: Map<String, Int>?) {
        throw UnsupportedOperationException("pinchOut is not supported for Compose elements")
    }

    override fun perform(action: ViewAction) {
        throw UnsupportedOperationException("Espresso ViewAction cannot be applied to a Compose element")
    }

    override fun isDisplayed(): Boolean {
        return try {
            node().isDisplayed()
        } catch (e: IllegalStateException) {
            if (e.message?.contains("No compose hierarchies found") == true) {
                false
            } else {
                throw e
            }
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            node().assertIsEnabled()
            true
        } catch (e: AssertionError) {
            false
        }
    }

    override fun isChecked(): Boolean {
        val config = node().fetchSemanticsNode().config
        config.getOrNull(SemanticsProperties.ToggleableState)?.let {
            return it == ToggleableState.On
        }
        config.getOrNull(SemanticsProperties.Selected)?.let {
            return it
        }
        throw CompospressoError(TAG, "Node has no toggleable/selected state")
    }

    override fun getText(): String {
        val config = node().fetchSemanticsNode().config
        config.getOrNull(SemanticsProperties.EditableText)?.let { return it.text }
        config.getOrNull(SemanticsProperties.Text)?.let { texts ->
            return texts.joinToString(separator = "") { it.text }
        }
        return ""
    }

    override fun waitDisplayed(timeOut: Long): Boolean {
        return try {
            ComposeTestRegistry.getRule()
                .waitUntil(timeOut) {
                    isDisplayed()
                }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun waitNotDisplayed(timeOut: Long): Boolean {
        return try {
            ComposeTestRegistry.getRule()
                .waitUntil(timeOut) {
                    !isDisplayed()
                }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getPoint(): Point {
        val bounds = node().fetchSemanticsNode().boundsInWindow
        return Point(bounds.left.toInt(), bounds.top.toInt())
    }
}
