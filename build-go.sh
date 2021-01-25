#!/usr/bin/env sh

mkdir -p build/go
go build -o ./build/go/java_faster_than_go ./src/main/go/java_faster_than_go.go ./src/main/go/util.go
go build -o ./build/go/almost_pseudo_random ./src/main/go/almost_pseudo_random.go ./src/main/go/util.go