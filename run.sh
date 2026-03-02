#!/bin/bash
set -e
cd "$(dirname "$0")/.."
OUT=seaBattle/out
JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home}"

if [ ! -d "$OUT" ]; then
  echo "Run build.sh first."
  exit 1
fi

"$JAVA_HOME/bin/java" -cp "$OUT" seaBattle.game.SeaBattleApp "$@"
