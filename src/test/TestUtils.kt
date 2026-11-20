package test

/**
 * Minimal test assertion utilities — no external dependencies.
 */

annotation class Test
annotation class AfterTest

class AssertionError(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

fun assertTrue(value: Boolean, message: String? = null) {
    if (!value) {
        throw AssertionError(message ?: "Expected true, but was false")
    }
}

fun assertFalse(value: Boolean, message: String? = null) {
    if (value) {
        throw AssertionError(message ?: "Expected false, but was true")
    }
}

fun assertEquals(expected: Any?, actual: Any?, message: String? = null) {
    if (expected != actual) {
        throw AssertionError(message ?: "Expected $expected, but was $actual")
    }
}

fun assertEquals(expected: Int, actual: Int, message: String? = null) {
    if (expected != actual) {
        throw AssertionError(message ?: "Expected $expected, but was $actual")
    }
}

fun assertNull(value: Any?, message: String? = null) {
    if (value != null) {
        throw AssertionError(message ?: "Expected null, but was $value")
    }
}

fun assertNotNull(value: Any?, message: String? = null): Any {
    if (value == null) {
        throw AssertionError(message ?: "Expected non-null value")
    }
    return value
}

fun assertThat(failure: Boolean, message: String) {
    if (failure) throw AssertionError(message)
}
