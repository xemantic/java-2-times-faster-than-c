#!/usr/bin/env sh

mkdir -p build/clang
gcc -O3 -o build/clang/almost_pseudo_random src/main/c/almost_pseudo_random.c -lm
gcc -O3 -o build/clang/java_2_times_faster_than_c src/main/c/java_2_times_faster_than_c.c -lm
