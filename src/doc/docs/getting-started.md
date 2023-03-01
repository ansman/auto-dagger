# Getting Started

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/se.ansman.dagger.auto/core.svg)](https://central.sonatype.com/search?smo=true&q=se.ansman.dagger.auto)

### Kotlin Modules
For pure Kotlin modules you just need to add two dependencies:
```kotlin
dependencies {
    // This assumes you've already added Hilt and Dagger as dependencies
    implementation("se.ansman.dagger.auto:core:{{gradle.version}}")
    kapt("se.ansman.dagger.auto:compiler:{{gradle.version}}")
}
```

### Android Modules
If your module is an Android module you should add the `android` dependency instead of `core`:
```kotlin
dependencies {
    // This assumes you've already added Hilt and Dagger as dependencies
    implementation("se.ansman.dagger.auto:android:{{gradle.version}}")
    kapt("se.ansman.dagger.auto:compiler:{{gradle.version}}")

    // Add these if you want to replace objects during tests
    testImplementation("se.ansman.dagger.auto:android-testing:{{gradle.version}}")
    kaptTest("se.ansman.dagger.auto:compiler:{{gradle.version}}")
}
```