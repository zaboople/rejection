#!/bin/bash
cd $(dirname $0)
./build.sh || exit 1
java -Xmx32m -classpath build "$@"
