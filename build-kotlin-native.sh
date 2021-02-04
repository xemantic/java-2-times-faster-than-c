#!/usr/bin/env sh

mkdir -p build/kotlin-native
kotlinc-native src/main/kotlin/KotlinAsFastAsJava.kt -Xallocator=mimalloc -opt -e com.xemantic.test.howfast.kotlin.main -o build/kotlin-native/KotlinAsFastAsJava
kotlinc-native src/main/kotlin/AlmostPseudoRandom.kt -Xallocator=mimalloc -opt -e com.xemantic.test.howfast.kotlin.main -o build/kotlin-native/AlmostPseudoRandom
