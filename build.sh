#!/bin/bash
mkdir -p bin
javac -d bin -sourcepath src src/app/MainApp.java
echo "Build complete. Run ./run.sh to start."
