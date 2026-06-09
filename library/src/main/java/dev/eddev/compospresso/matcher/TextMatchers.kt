package dev.eddev.compospresso.matcher

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.AnyOf.anyOf

fun withCompatText(string: String): Matcher<View> {
    return anyOf(
        withText(string),
        allOf(
            ViewMatchers.withParent(ViewMatchers.isAssignableFrom(TextInputLayout::class.java)),
            ViewMatchers.hasDescendant(withText(string))
        )
    )
}

fun withCompatText(stringMatcher: Matcher<String>): Matcher<View> {
    return Matchers.anyOf(
        ViewMatchers.withText(stringMatcher),
        Matchers.allOf(
            ViewMatchers.withParent(ViewMatchers.isAssignableFrom(TextInputLayout::class.java)),
            ViewMatchers.hasDescendant(ViewMatchers.withText(stringMatcher))
        )
    )
}

fun ViewInteraction.getText(): String {
    var text: String = ""
    this.perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(TextView::class.java)
        }

        override fun getDescription(): String {
            return "TextView text"
        }

        override fun perform(
            uiController: UiController?,
            view: View?
        ) {
            val textView = view as TextView
            text = textView.text.toString()
        }
    })
    return text
}

fun itemAtRecyclerViewPositionWithText(
    position: Int,
    expectedText: String,
    textViewId: Int
): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("with text at position $position: $expectedText")
        }

        override fun matchesSafely(recyclerView: RecyclerView): Boolean {
            val viewHolder = recyclerView.findViewHolderForLayoutPosition(position)
            val textView = viewHolder?.itemView?.findViewById<TextView>(textViewId)
            return textView?.text.toString() == expectedText
        }
    }
}
