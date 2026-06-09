package dev.eddev.compospresso.assertion

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withText
import dev.eddev.compospresso.matcher.resourceMatcher
import dev.eddev.compospresso.uielement.UiElement
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals

fun assertHasText(viewMatcher: Matcher<View>, text: String) {
    viewMatcher.assertAny(withText(text))
}

internal fun assertHasText(viewId: Int, text: String) {
    assertHasText(viewId.resourceMatcher(), text)
}

fun assertHasText(uiElement: UiElement, text: String) {
    val actualText = try {
        uiElement.getText()
    } catch (e: java.lang.Exception) {
        e.message
    }
    assertEquals(
        "UiElement is expected to have text [$text]" +
            "\nActual text [$actualText]", text, uiElement.getText()
    )
}
