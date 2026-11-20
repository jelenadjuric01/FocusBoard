package test

import kotlin.reflect.full.memberFunctions

fun runTestClass(className: String): Int {
    val kClass = Class.forName(className).kotlin
    val instance = kClass.createInstance() as Any
    val testMethods = kClass.memberFunctions.filter {
        it.annotations.any { ann -> ann.annotationClass == Test::class }
    }

    var passed = 0
    var failed = 0

    for (method in testMethods) {
        val testName = method.name
        try {
            // Run cleanup before each test
            val cleanupMethod = kClass.memberFunctions.find { it.name == "cleanup" }
            cleanupMethod?.call(instance)

            method.call(instance)
            println("  ✓ $testName")
            passed++
        } catch (e: Exception) {
            println("  ✗ $testName")
            println("    ${e.message}")
            failed++
        }
    }

    println("  $passed passed, $failed failed")
    return failed
}

fun main(args: Array<String>) {
    val testClasses = if (args.isEmpty()) {
        listOf("test.TaskTest", "test.TaskStoreTest")
    } else {
        args.toList()
    }

    var totalFailed = 0
    for (testClass in testClasses) {
        println("Running $testClass...")
        val failed = runTestClass(testClass)
        totalFailed += failed
        println()
    }

    if (totalFailed > 0) {
        println("FAILED: $totalFailed test(s) failed")
        System.exit(1)
    } else {
        println("All tests passed!")
    }
}
