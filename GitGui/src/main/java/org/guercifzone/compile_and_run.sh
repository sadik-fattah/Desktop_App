#!/bin/bash

# Compile the Java files
echo "Compiling Java files..."
mkdir -p bin
javac -d bin src/com/gitgui/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Running Git GUI..."
    java -cp bin com.gitgui.GitGUI
else
    echo "Compilation failed!"
    exit 1
fi