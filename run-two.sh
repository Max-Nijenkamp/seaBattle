#!/bin/bash
set -e
cd "$(dirname "$0")/.."
OUT=seaBattle/out
JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home}"

if [ ! -d "$OUT" ]; then
  echo "Building first..."
  ./seaBattle/build.sh
fi

echo "Starting instance 1..."
"$JAVA_HOME/bin/java" -cp "$OUT" seaBattle.game.SeaBattleApp &
PID1=$!
sleep 2
echo "Starting instance 2..."
"$JAVA_HOME/bin/java" -cp "$OUT" seaBattle.game.SeaBattleApp &
PID2=$!
