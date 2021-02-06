#!/usr/bin/env sh

mkdir -p build/rust
cargo build --release --manifest-path src/main/rust/Cargo.toml
find src/main/rust/target/release \
  -maxdepth 1 \
  -executable \
  -type f \
  -exec cp -f {} build/rust \;
