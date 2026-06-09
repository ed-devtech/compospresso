package dev.eddev.compospresso.internal

/**
 * Extension property to use a class's simple name as a log tag.
 * Truncated to 23 chars to satisfy Logcat's tag length limit on older platforms.
 * Use: CompospressoLog.i(TAG, "message")
 */
val Any.TAG: String
    get() {
        val tag = this.javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }
