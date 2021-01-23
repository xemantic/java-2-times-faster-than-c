#!/usr/bin/env sh

mkdir -p build/csharp
dotnet publish -c Release -o ./build/csharp ./src/main/csharp/java-4-times-faster-than-c-sharp.csproj
