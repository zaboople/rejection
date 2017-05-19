#!/bin/bash
cd $(dirname $0)
java -Xmx28m -classpath build main.Boot "$@"
