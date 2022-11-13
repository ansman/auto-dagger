# Getting Started

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/ansman/deager.svg)](https://maven-badges.herokuapp.com/maven-central/ansman/deager)

### Kotlin Modules
When using a plain Kotlin module you just need to add two dependencies:
```kotlin
dependencies {
    // This assumes you've already added Hilt and Dagger as dependencies
    implementation("se.ansman.deager:core:{{gradle.version}}")
    kapt("se.ansman.deager:compiler:{{gradle.version}}")
}
```

### Android Modules
If your module is an Android module you should instead add the `android` dependency:
```kotlin
dependencies {
    // This assumes you've already added Hilt and Dagger as dependencies
    implementation("se.ansman.deager:android:{{gradle.version}}")
    kapt("se.ansman.deager:compiler:{{gradle.version}}")
}
```