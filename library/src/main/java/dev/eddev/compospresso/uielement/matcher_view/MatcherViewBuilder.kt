package dev.eddev.compospresso.uielement.matcher_view

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import dev.eddev.compospresso.matcher.resourceMatcher
import dev.eddev.compospresso.matcher.withCompatText
import dev.eddev.compospresso.uielement.MatcherViewElement
import dev.eddev.compospresso.uielement.toElement
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class MatcherViewBuilder {

    private val matchers = mutableListOf<Matcher<View>>()

    constructor()
    constructor(matcher: Matcher<View>) : this() {
        matchers.add(matcher)
    }
    constructor(resId: Int) : this(resId.resourceMatcher())

    fun with(vararg resIds: Int) = apply {
        matchers.add(resIds.resourceMatcher())
    }

    fun with(matcher: Matcher<View>) = apply {
        matchers.add(matcher)
    }

    fun withText(text: String) = apply {
        matchers.add(withCompatText(text))
    }

    fun containsText(text: String) = apply {
        matchers.add(ViewMatchers.withText(Matchers.containsString(text)))
    }

    fun parent(parent: Matcher<View>) = apply {
        matchers.add(ViewMatchers.withParent(parent))
    }

    fun parent(vararg resIds: Int) = apply {
        matchers.add(ViewMatchers.withParent(resIds.resourceMatcher()))
    }

    fun withChild(child: Matcher<View>) = apply {
        matchers.add(ViewMatchers.withChild(child))
    }

    fun withChild(vararg resIds: Int) = apply {
        matchers.add(ViewMatchers.withChild(resIds.resourceMatcher()))
    }

    fun withDescendant(descendant: Matcher<View>) = apply {
        matchers.add(Matchers.allOf(descendant, ViewMatchers.isDescendantOfA(Matchers.allOf(matchers))))
    }

    fun withDescendant(resId: Int) = apply {
        withDescendant(resId.resourceMatcher())
    }

    fun withAncestor(ancestor: Matcher<View>) = apply {
        matchers.add(ViewMatchers.isDescendantOfA(ancestor))
    }

    fun withAncestor(resId: Int) = apply {
        withAncestor(resId.resourceMatcher())
    }

    fun hasSibling(resId: Int) = apply {
        matchers.add(ViewMatchers.hasSibling(resId.resourceMatcher()))
    }

    fun hasSibling(matcher: Matcher<View>) = apply {
        matchers.add(matcher)
    }

    fun buildMatcher(): MatcherView = object : MatcherView {
        override fun toMatcher(): Matcher<View> = Matchers.allOf(matchers)
    }

    fun buildElement(): MatcherViewElement = buildMatcher().toElement()
}
