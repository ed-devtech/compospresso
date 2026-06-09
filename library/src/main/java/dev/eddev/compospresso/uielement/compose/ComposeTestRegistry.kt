package dev.eddev.compospresso.uielement.compose

import androidx.compose.ui.test.junit4.ComposeTestRule

object ComposeTestRegistry {
    private lateinit var rule: ComposeTestRule

    fun initialize(testRule: ComposeTestRule) {
        rule = testRule
    }

    fun getRule(): ComposeTestRule {
        if (!::rule.isInitialized) {
            throw IllegalStateException("ComposeTestRegistry not initialized")
        }
        return rule
    }
}
