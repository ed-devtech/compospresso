package dev.eddev.compospresso.uielement.matcher_view

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import dev.eddev.compospresso.matcher.resourceMatcher
import dev.eddev.compospresso.matcher.withCompatText
import dev.eddev.compospresso.uielement.matcher_view.MatcherView.Companion.builder
import org.hamcrest.Matcher
import org.hamcrest.Matchers

@Suppress("FunctionName")
fun MatcherView(matcher: Matcher<View>) = object : MatcherView {
    override fun toMatcher(): Matcher<View> = matcher
}

@Suppress("FunctionName")
fun MatcherView(vararg resIds: Int) = MatcherView(resIds.resourceMatcher())

@Suppress("FunctionName")
fun MatcherView(text: String) = MatcherView(withCompatText(text))

@Suppress("FunctionName")
fun MatcherView(
    resId: Int,
    text: String
) = builder()
    .with(resId)
    .withText(text)
    .buildMatcher()

interface MatcherView {
    fun toMatcher(): Matcher<View>

    companion object {

        fun withContentDescription(desc: String): MatcherView = object : MatcherView {
            override fun toMatcher() = ViewMatchers.withContentDescription(desc)
        }

        fun withResId(resIds: Int): MatcherView = object : MatcherView {
            override fun toMatcher() = resIds.resourceMatcher()
        }

        fun withResIdAndText(
            resId: Int,
            text: String,
            exactMatch: Boolean = true,
        ): MatcherView = object : MatcherView {
            override fun toMatcher(): Matcher<View> {
                val withTextMatcher = when {
                    exactMatch -> ViewMatchers.withText(text)
                    else -> ViewMatchers.withText(Matchers.containsString(text))
                }
                return Matchers.allOf(resId.resourceMatcher(), withTextMatcher)
            }
        }

        fun withResIdAndContentDescription(
            resId: Int,
            contentDescription: String,
            exactMatch: Boolean = true,
        ): MatcherView = object : MatcherView {
            override fun toMatcher(): Matcher<View> {
                val withContentDescriptionMatcher = when {
                    exactMatch -> ViewMatchers.withContentDescription(contentDescription)
                    else -> ViewMatchers.withContentDescription(
                        Matchers.containsString(
                            contentDescription
                        )
                    )
                }
                return Matchers.allOf(resId.resourceMatcher(), withContentDescriptionMatcher)
            }
        }

        fun builder() = MatcherViewBuilder()
        fun builder(matcher: Matcher<View>) = MatcherViewBuilder(matcher)
        fun builder(resId: Int) = MatcherViewBuilder(resId)
    }
}
