package dev.eddev.compospresso.interaction

import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import dev.eddev.compospresso.internal.TAG
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeoutException
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any

private const val LOOP_DURATION = 100L

/**
 * Special version of `onView`.
 * Sometimes onView produces an exception because the view isn't ready on the hierarchy.
 * It will wait for the view to exist and then resolve using `onView`.
 */
fun waitForVisibility(
    viewMatcher: Matcher<View>,
    waitTimeOut: Long
) {
    val latch = CountDownLatch(1)
    val waitAction: ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isRoot()
        }

        override fun getDescription(): String {
            return "Wait for a view with id <$viewMatcher> to exist, during $waitTimeOut millis."
        }

        override fun perform(
            uiController: UiController,
            view: View
        ) {
            uiController.loopMainThreadUntilIdle()

            val startTime = System.currentTimeMillis()
            val endTime = startTime + waitTimeOut
            do {
                uiController.loopMainThreadForAtLeast(LOOP_DURATION)
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child) && child.visibility == ViewMatchers.Visibility.VISIBLE.value) {
                        Log.i(TAG, "view with id <$viewMatcher> found")
                        latch.countDown()
                        return
                    }
                }
            } while (System.currentTimeMillis() < endTime)

            // Not found
            throw PerformException.Builder()
                .withActionDescription(description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(TimeoutException())
                .build()
        }
    }
    Espresso.onView(ViewMatchers.isRoot()).perform(waitAction)
}

/**
 * A [ViewAction] that waits up to [timeout] milliseconds for a [View]'s visibility value to change to [View.GONE].
 */
fun waitUntilGoneAction(timeout: Long) = object : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return any(View::class.java)
    }

    override fun getDescription(): String {
        return "wait up to $timeout milliseconds for the view to be gone"
    }

    override fun perform(
        uiController: UiController,
        view: View
    ) {
        val endTime = System.currentTimeMillis() + timeout
        do {
            if (view.visibility == View.GONE) return
            uiController.loopMainThreadForAtLeast(50)
        } while (System.currentTimeMillis() < endTime)

        throw PerformException.Builder()
            .withActionDescription(description)
            .withCause(TimeoutException("Waited $timeout milliseconds"))
            .withViewDescription(HumanReadables.describe(view))
            .build()
    }
}
