#!/bin/bash
cd $(dirname $0)
build.sh || exit 1
java -classpath build "$@"
