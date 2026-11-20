#!/bin/bash
# FocusBoard Test Runner
# Usage: ./run_tests.sh

set -e

SRC_DIR="src/main"
TEST_DIR="src/test"
BUILD_DIR="build"

# Clean build directory
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"
mkdir -p "$BUILD_DIR/test-classes"

# Find kotlin source files
SRC_FILES=$(find "$SRC_DIR" -name "*.kt")
TEST_FILES=$(find "$TEST_DIR" -name "*.kt")

echo "=== Compiling main sources ==="
kotlinc -d "$BUILD_DIR/classes" $SRC_FILES
echo "=== Main sources compiled ==="

echo ""
echo "=== Compiling test sources ==="
kotlinc -cp "$BUILD_DIR/classes" -d "$BUILD_DIR/test-classes" $TEST_FILES
echo "=== Test sources compiled ==="

echo ""
echo "=== Running tests ==="

# Run tests using a simple test runner
cat > "$BUILD_DIR/RunTests.kt" << 'EOF'
import test.TaskTest
import test.TaskStoreTest
import kotlin.test.Test
import kotlin.reflect.full.memberFunctions

fun runTests(testClass: KClass<*>) {
    val instance = testClass.objectInstance ?: testClass.createInstance()
    val methods = testClass.memberFunctions.filter { it.annotations.any { ann -> ann is Test } }
    
    var passed = 0
    var failed = 0
    
    for (method in methods) {
        val testName = method.name
        try {
            // Call @AfterTest cleanup before each test
            val afterTest = testClass.memberFunctions.find { it.name == "cleanup" }
            afterTest?.call(instance)
            
            method.call(instance)
            println("  ✓ $testName")
            passed++
        } catch (e: Exception) {
            println("  ✗ $testName")
            println("    ${e.message}")
            failed++
        }
    }
    
    println("$passed passed, $failed failed")
    if (failed > 0) throw AssertionError("$failed test(s) failed")
}

fun main() {
    println("Running TaskTest...")
    runTests(TaskTest::class)
    println()
    println("Running TaskStoreTest...")
    runTests(TaskStoreTest::class)
    println()
    println("All tests passed!")
}
EOF

kotlinc -cp "$BUILD_DIR/classes:$BUILD_DIR/test-classes" -d "$BUILD_DIR/test-runner" \
  "$BUILD_DIR/RunTests.kt" \
  -include-runtime

kotlin -jar "$BUILD_DIR/test-runner/RunTests.jar"

echo ""
echo "=== All tests passed ==="
