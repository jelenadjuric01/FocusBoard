package test

/**
 * Simple test runner without reflection — tests are registered explicitly.
 */

typealias TestFunction = () -> Unit

data class TestEntry(
    val name: String,
    val function: TestFunction
)

class TestSuite(
    val name: String,
    val tests: List<TestEntry>,
    val cleanup: (() -> Unit)? = null
)

object TestRunner {
    private val suites = mutableListOf<TestSuite>()

    fun register(suite: TestSuite) {
        suites.add(suite)
    }

    fun runAll(): Boolean {
        var totalPassed = 0
        var totalFailed = 0

        for (suite in suites) {
            println("Running ${suite.name}...")
            var suitePassed = 0
            var suiteFailed = 0
            for (test in suite.tests) {
                try {
                    suite.cleanup?.invoke()
                    test.function.invoke()
                    println("  ✓ ${test.name}")
                    suitePassed++
                    totalPassed++
                } catch (e: Exception) {
                    println("  ✗ ${test.name}")
                    println("    ${e.message}")
                    suiteFailed++
                    totalFailed++
                }
            }
            println("  $suitePassed passed, $suiteFailed failed")
            println()
        }

        println("Total: $totalPassed passed, $totalFailed failed")
        return totalFailed == 0
    }
}

class TestRunnerMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Register all test suites
            TestRunner.register(TaskTest.suite)
            TestRunner.register(TaskStoreTest.suite)

            val success = TestRunner.runAll()
            if (!success) {
                System.exit(1)
            } else {
                println("All tests passed!")
            }
        }
    }
}
