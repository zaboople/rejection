#!/bin/bash
rm -rf build
mkdir -p build
javac -d build java/*.java
java -classpath build DeckTest 2  5 5 5 5 5 5  3 3 3 3  2
