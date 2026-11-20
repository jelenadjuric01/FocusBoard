#!/bin/bash
# FocusBoard Build Script
# Usage: ./build.sh

set -e

SRC_DIR="src/main"
BUILD_DIR="build"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

SRC_FILES=$(find "$SRC_DIR" -name "*.kt")

echo "=== Building FocusBoard ==="
kotlinc -include-runtime -d "$BUILD_DIR/FocusBoard.jar" $SRC_FILES
echo "=== Build complete: $BUILD_DIR/FocusBoard.jar ==="
