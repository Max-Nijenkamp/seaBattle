#!/bin/bash
set -e
cd "$(dirname "$0")/.."
SRC=seaBattle
OUT=seaBattle/out
JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home}"

mkdir -p "$OUT"
"$JAVA_HOME/bin/javac" -d "$OUT" \
  "$SRC/game/"*.java "$SRC/ui/"*.java "$SRC/network/"*.java
