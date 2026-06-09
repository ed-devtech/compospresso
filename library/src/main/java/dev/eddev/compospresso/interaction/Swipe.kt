package dev.eddev.compospresso.interaction

import android.view.View
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import dev.eddev.compospresso.matcher.resourceMatcher
import org.hamcrest.Matcher

fun swipe(matcher: Matcher<View>, startCoord: CoordinatesProvider, endCoord: CoordinatesProvider) {
    matcher.performAction(GeneralSwipeAction(Swipe.SLOW, startCoord, endCoord, Press.FINGER))
}

fun swipe(resId: Int, startCoord: CoordinatesProvider, endCoord: CoordinatesProvider) {
    swipe(resId.resourceMatcher(), startCoord, endCoord)
}

fun swipeLeft(matcher: Matcher<View>) {
    matcher.performAction(ViewActions.swipeLeft())
}

enum class Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN,
}
