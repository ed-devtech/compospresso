package dev.eddev.compospresso.uielement

import android.graphics.Point
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.performClick
import androidx.test.espresso.ViewAction
import dev.eddev.compospresso.interaction.Direction
import dev.eddev.compospresso.uielement.compose.ComposeMatcher
import dev.eddev.compospresso.uielement.compose.ComposeTestRegistry

class ComposeElement(private val composeMatcher: ComposeMatcher) : UiElement {
    private fun node() = composeMatcher.toNodeInteraction()

    override fun tap(timeOut: Long) {
        node().performClick()
    }

    override fun longTap() {
        TODO("Not yet implemented")
    }

    override fun doubleTap() {
        TODO("Not yet implemented")
    }

    override fun putText(
        text: String,
        timeOut: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun clearTextInput() {
        TODO("Not yet implemented")
    }

    override fun setDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun swipe(direction: Direction) {
        TODO("Not yet implemented")
    }

    override fun pinchIn(params: Map<String, Int>?) {
        TODO("Not yet implemented")
    }

    override fun pinchOut(params: Map<String, Int>?) {
        TODO("Not yet implemented")
    }

    override fun perform(action: ViewAction) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun isChecked(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getText(): String {
        TODO("Not yet implemented")
    }

    override fun waitDisplayed(timeOut: Long): Boolean {
        return try {
            ComposeTestRegistry.getRule().waitUntil(timeOut) {
                isDisplayed()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun waitNotDisplayed(timeOut: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPoint(): Point {
        TODO("Not yet implemented")
    }
}
