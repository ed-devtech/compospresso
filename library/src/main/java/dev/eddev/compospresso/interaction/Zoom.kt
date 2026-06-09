package dev.eddev.compospresso.interaction

import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import org.hamcrest.Matcher

fun pinchIn(): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isDisplayingAtLeast(90)
        }

        override fun getDescription(): String {
            return "Pinch in"
        }

        override fun perform(
            uiController: UiController,
            view: View
        ) {
            val middleX = view.width / 2.0f
            val middleY = view.height / 2.0f

            val startX1 = middleX - 100
            val startY1 = middleY - 100
            val startX2 = middleX + 100
            val startY2 = middleY + 100

            val endX1 = middleX
            val endY1 = middleY
            val endX2 = middleX
            val endY2 = middleY

            val events = listOf(
                MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, startX1, startY1, 0),
                MotionEvent.obtain(0, 0, MotionEvent.ACTION_POINTER_DOWN, startX2, startY2, 0),
                MotionEvent.obtain(0, 200, MotionEvent.ACTION_MOVE, endX1, endY1, 0),
                MotionEvent.obtain(0, 200, MotionEvent.ACTION_MOVE, endX2, endY2, 0),
                MotionEvent.obtain(0, 400, MotionEvent.ACTION_POINTER_UP, endX2, endY2, 0),
                MotionEvent.obtain(0, 400, MotionEvent.ACTION_UP, endX1, endY1, 0)
            )

            events.forEach { view.dispatchTouchEvent(it) }
            uiController.loopMainThreadForAtLeast(500)
        }
    }
}

// Zoom in
fun pinchOut(): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isDisplayingAtLeast(90)
        }

        override fun getDescription(): String {
            return "Pinch out"
        }

        override fun perform(
            uiController: UiController,
            view: View
        ) {
            val middleX = view.width / 2.0f
            val middleY = view.height / 2.0f

            val startX1 = middleX
            val startY1 = middleY
            val startX2 = middleX
            val startY2 = middleY

            val endX1 = middleX - 100
            val endY1 = middleY - 100
            val endX2 = middleX + 100
            val endY2 = middleY + 100

            val events = listOf(
                MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, startX1, startY1, 0),
                MotionEvent.obtain(0, 0, MotionEvent.ACTION_POINTER_DOWN, startX2, startY2, 0),
                MotionEvent.obtain(0, 200, MotionEvent.ACTION_MOVE, endX1, endY1, 0),
                MotionEvent.obtain(0, 200, MotionEvent.ACTION_MOVE, endX2, endY2, 0),
                MotionEvent.obtain(0, 400, MotionEvent.ACTION_POINTER_UP, endX2, endY2, 0),
                MotionEvent.obtain(0, 400, MotionEvent.ACTION_UP, endX1, endY1, 0)
            )

            events.forEach { view.dispatchTouchEvent(it) }
            uiController.loopMainThreadForAtLeast(500)
        }
    }
}
