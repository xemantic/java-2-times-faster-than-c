#!/usr/bin/env sh

mkdir -p build/csharp
dotnet publish -c Release -o ./build/csharp ./src/main/csharp/java-faster-csharp/java-faster-csharp.csproj
dotnet publish -c Release -o ./build/csharp ./src/main/csharp/xorshift_rng/xorshift_rng.csproj
