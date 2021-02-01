#!/usr/bin/env sh

mkdir -p build/rust
rustc -O -o build/rust/almost_pseudo_random src/main/rust/almost_pseudo_random.rs
rustc -O -o build/rust/rust_faster_than_java src/main/rust/rust_faster_than_java.rs
rustc -O -o build/rust/rust_raw_pointers_faster_than_java src/main/rust/rust_raw_pointers_faster_than_java.rs
