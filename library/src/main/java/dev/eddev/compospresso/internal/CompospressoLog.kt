package dev.eddev.compospresso.internal

import android.annotation.SuppressLint
import android.util.Log

/**
 * Minimal log facade used by Compospresso interactions and decorators.
 * Wraps android.util.Log. No file output, no metrics — pure logcat.
 */
@SuppressLint("WrongLogDetector")
object CompospressoLog {

    private const val LOG_TAG = "Compospresso"

    fun i(tag: String, message: String) {
        Log.i(LOG_TAG, "$tag: $message")
    }

    fun w(tag: String, message: String) {
        Log.w(LOG_TAG, "$tag: $message")
    }

    fun e(tag: String, message: String) {
        Log.e(LOG_TAG, "$tag: $message")
    }

    fun e(tag: String, message: String, tr: Throwable) {
        Log.e(LOG_TAG, "$tag: $message", tr)
    }
}
