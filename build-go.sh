#!/usr/bin/env sh

mkdir -p build/go
go build -o ./build/go/java_faster_than_go ./src/main/go/java_faster_than_go.go
go build -o ./build/go/xorshift_rng ./src/main/go/xorshift_rng.go
