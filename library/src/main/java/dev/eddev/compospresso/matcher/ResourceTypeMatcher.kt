package dev.eddev.compospresso.matcher

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

internal enum class ResourceType {
    ID,
    STRING
}

internal val Int.resourceType: ResourceType
    get() {
        return when (
            val resourceTypeName = ApplicationProvider
                .getApplicationContext<Context>().resources.getResourceTypeName(this)
        ) {
            "id" -> ResourceType.ID
            "string" -> ResourceType.STRING
            else -> throw ResourceTypeException(
                "The id argument must be R.id.* or R.string.*, but was $resourceTypeName"
            )
        }
    }

internal fun Int.resourceMatcher(): Matcher<View> = when (resourceType) {
    ResourceType.ID -> withId(this)
    ResourceType.STRING -> withText(this)
}

internal fun IntArray.resourceMatcher(): Matcher<View> {
    val matchers = mutableListOf<Matcher<View>>().also { matchers ->
        this.forEach { resId -> matchers.add(resId.resourceMatcher()) }
    }
    return allOf(matchers)
}

internal class ResourceTypeException(message: String) : RuntimeException(message)
