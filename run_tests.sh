#!/bin/bash
# FocusBoard Test Runner
# Usage: ./run_tests.sh

set -e

KOTLIN_HOME="${KOTLIN_HOME:-/Applications/IntelliJ IDEA.app/Contents/plugins/Kotlin/kotlinc}"
KOTLINC="$KOTLIN_HOME/bin/kotlinc"
KOTLIN="$KOTLIN_HOME/bin/kotlin"

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
"$KOTLINC" -d "$BUILD_DIR/classes" $SRC_FILES
echo "=== Main sources compiled ==="

echo ""
echo "=== Compiling test sources ==="
"$KOTLINC" -cp "$BUILD_DIR/classes" -d "$BUILD_DIR/test-classes" $TEST_FILES
echo "=== Test sources compiled ==="

echo ""
echo "=== Running tests ==="
"$KOTLIN" -cp "$BUILD_DIR/classes:$BUILD_DIR/test-classes" test.TestRunnerMain

echo ""
echo "=== All tests passed ==="
