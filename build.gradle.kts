import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.21"
}

group = "com.xemantic.test"
version = "1.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_15
  targetCompatibility = JavaVersion.VERSION_15
}
dependencies {
  implementation(kotlin("stdlib-jdk8"))
}
repositories {
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = "15"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = "15"
}
