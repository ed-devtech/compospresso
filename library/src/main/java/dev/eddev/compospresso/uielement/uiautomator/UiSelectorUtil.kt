package dev.eddev.compospresso.uielement.uiautomator

import androidx.test.uiautomator.UiSelector

fun UiSelector.withResIdAndText(
    resId: String,
    text: String
) = this.resourceId(resId).text(text)
