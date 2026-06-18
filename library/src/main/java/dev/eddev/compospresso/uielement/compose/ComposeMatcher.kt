package dev.eddev.compospresso.uielement.compose

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText

interface ComposeMatcher {
    fun toNodeInteraction(): SemanticsNodeInteraction

    companion object {
        fun text(text: String): ComposeMatcher = object : ComposeMatcher {
            override fun toNodeInteraction(): SemanticsNodeInteraction = ComposeTestRegistry.getRule()
                .onNodeWithText(text)
        }

        fun contentDesc(desc: String): ComposeMatcher = object : ComposeMatcher {
            override fun toNodeInteraction(): SemanticsNodeInteraction = ComposeTestRegistry.getRule()
                .onNodeWithContentDescription(desc)
        }

        fun lastContentDesc(desc: String): ComposeMatcher = object : ComposeMatcher {
            override fun toNodeInteraction(): SemanticsNodeInteraction {
                return ComposeTestRegistry
                    .getRule()
                    .onAllNodesWithContentDescription(desc)
                    .let { interactionCollection ->
                        val nodes = interactionCollection.fetchSemanticsNodes()
                        require(nodes.isNotEmpty()) { "No nodes found with content description: $desc" }
                        interactionCollection[nodes.lastIndex]
                    }
            }
        }

        fun testTag(tag: String): ComposeMatcher = object : ComposeMatcher {
            override fun toNodeInteraction(): SemanticsNodeInteraction {
                return ComposeTestRegistry
                    .getRule()
                    .onNodeWithTag(tag)
            }
        }
    }
}
