#!/bin/bash
cd $(dirname $0)
java -Xmx20m -classpath build main.Boot "$@"
