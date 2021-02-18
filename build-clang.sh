#!/usr/bin/env sh

mkdir -p build/clang
clang -O3 -o build/clang/xorshift_rng src/main/c/xorshift_rng.c
clang -O3 -o build/clang/java_2_times_faster_than_c src/main/c/java_2_times_faster_than_c.c -lm
