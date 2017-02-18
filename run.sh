#!/bin/bash
echo "Compiling..."
rm -rf build
mkdir -p build
javac -d build java/*.java
echo "Running..."
java -classpath build AsciiBoard 1 1 1 1 1 1 94
