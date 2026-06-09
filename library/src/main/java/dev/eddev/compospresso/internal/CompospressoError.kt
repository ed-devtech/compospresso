package dev.eddev.compospresso.internal

/**
 * Single error type for test-side failures raised by Compospresso interactions and assertions.
 * Replaces both Doppio's DoppioError and L360 testsupport's TestError.
 */
open class CompospressoError(tag: String, message: String) : Error("$tag: $message")
