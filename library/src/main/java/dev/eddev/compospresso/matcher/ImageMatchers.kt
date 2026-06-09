package dev.eddev.compospresso.matcher

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Matcher helper to compare imageview
 * Use: onView(withId(imageViewId)).matches(toDrawable(expectedDrawable))
 */
fun withBitmapDrawable(@DrawableRes drawableResId: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("with drawable from resource id: ")
            description.appendValue(drawableResId)
        }

        @SuppressLint("ResourceType")
        override fun matchesSafely(target: View?): Boolean {
            if (target !is ImageView) {
                return false
            }
            if (drawableResId < 0) {
                return target.drawable == null
            }
            val expectedDrawable = ContextCompat.getDrawable(target.context, drawableResId)
                ?: return false

            val bitmap = (target.drawable as BitmapDrawable).bitmap
            val otherBitmap = (expectedDrawable as BitmapDrawable).bitmap
            return bitmap.sameAs(otherBitmap)
        }
    }
}

/**
 * Espresso helper to compare imageview with DrawableVector
 * Use: onView(withId(imageViewId)).matches(withDrawableVector(expectedDrawable))
 */
fun withVectorDrawable(@DrawableRes drawableResId: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("with drawable from resource id: ")
            description.appendValue(drawableResId)
        }

        @SuppressLint("ResourceType")
        override fun matchesSafely(target: View?): Boolean {
            if (target !is ImageView) {
                return false
            }
            if (drawableResId < 0) {
                return target.drawable == null
            }
            val expectedDrawable = ContextCompat.getDrawable(target.context, drawableResId)
                ?: return false

            val bitmap = Bitmap.createBitmap(
                target.drawable.intrinsicWidth,
                target.drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888,
            )
            val newExpectedDrawable = Bitmap.createBitmap(
                expectedDrawable.intrinsicWidth,
                expectedDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888,
            )
            return bitmap.sameAs(newExpectedDrawable)
        }
    }
}
