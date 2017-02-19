#!/bin/bash
cd $(dirname $0)
rm -rf build
mkdir -p build
find java -type f | xargs javac -d build

