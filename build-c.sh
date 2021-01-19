#!/usr/bin/env sh

mkdir -p build/c
gcc -O3 -o build/c/java_2_times_faster_than_c src/main/c/java_2_times_faster_than_c.c
