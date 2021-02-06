#!/usr/bin/env sh

mkdir -p build/go
go build -o ./build/go/java_faster_than_go ./src/main/go/java_faster_than_go.go
go build -o ./build/go/almost_pseudo_random ./src/main/go/almost_pseudo_random.go
go build -o ./build/go/xorshift_rng ./src/main/go/xorshift_rng.go
go build -o ./build/go/java_faster_than_go_xorshift ./src/main/go/java_faster_than_go_xorshift.go
