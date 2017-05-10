#!/bin/bash
cd $(dirname $0)
java -Xmx24m -classpath build main.Boot "$@"
