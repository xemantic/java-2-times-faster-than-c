#!/usr/bin/env sh

mkdir -p build/rust
rustc -O -o build/rust/almost_pseudo_random src/main/rust/almost_pseudo_random.rs
rustc -O -o build/rust/xorshift_rng src/main/rust/xorshift_rng.rs
rustc -O -o build/rust/rust_safer src/main/rust/rust_safer.rs
rustc -O -o build/rust/rust_safer_xorshift src/main/rust/rust_safer_xorshift.rs
rustc -O -o build/rust/rust_raw_xorshift src/main/rust/rust_raw_xorshift.rs
rustc -O -o build/rust/rust_raw src/main/rust/rust_raw.rs
