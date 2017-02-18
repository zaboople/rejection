#!/bin/bash
rm -rf build
mkdir -p build
find java -type f | xargs javac -d build
java -classpath build test.AsciiBoardTest "$@"
