package dev.eddev.compospresso.interaction

import android.view.View
import android.widget.DatePicker
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

fun setDatePickerDate(
    year: Int,
    month: Int,
    dayOfMonth: Int
): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return allOf(ViewMatchers.isDisplayed(), ViewMatchers.isAssignableFrom(DatePicker::class.java))
        }

        override fun getDescription(): String {
            return "Set the date of DatePicker to: $year/$month/$dayOfMonth"
        }

        override fun perform(
            uiController: UiController?,
            view: View?
        ) {
            val datePicker = view as DatePicker
            datePicker.updateDate(year, month, dayOfMonth)
        }
    }
}
