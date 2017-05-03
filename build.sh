#!/bin/bash
cd $(dirname $0)
rm -rf build
mkdir -p build
find java -name '*.java' | xargs javac -d build

