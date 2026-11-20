package test

/**
 * Minimal test assertion utilities — no external dependencies.
 */

class TestAssertionError(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

fun assertTrue(value: Boolean, message: String? = null) {
    if (!value) {
        throw TestAssertionError(message ?: "Expected true, but was false")
    }
}

fun assertFalse(value: Boolean, message: String? = null) {
    if (value) {
        throw TestAssertionError(message ?: "Expected false, but was true")
    }
}

fun assertEquals(expected: Any?, actual: Any?, message: String? = null) {
    if (expected != actual) {
        throw TestAssertionError(message ?: "Expected $expected, but was $actual")
    }
}

fun assertEquals(expected: Int, actual: Int, message: String? = null) {
    if (expected != actual) {
        throw TestAssertionError(message ?: "Expected $expected, but was $actual")
    }
}

