# Getting Started

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/se.ansman.dagger.auto/core.svg)](https://central.sonatype.com/search?smo=true&q=se.ansman.dagger.auto)

### Android Modules
For pure Kotlin modules you need to add KAPT, the auto-dagger android dependency, the auto-dagger compiler as well as 
the hilt dependencies:
```kotlin
plugins {
    kotlin("kapt") // For Groovy build scripts use id('kotlin-kapt')
    id("com.google.dagger.hilt.android") // Optional, but recommended
}

dependencies {
    implementation("se.ansman.dagger.auto:android:{{gradle.version}}")
    kapt("se.ansman.dagger.auto:compiler:{{gradle.version}}")

    // Add these if you want to replace objects during tests
    testImplementation("se.ansman.dagger.auto:android-testing:{{gradle.version}}")
    kaptTest("se.ansman.dagger.auto:compiler:{{gradle.version}}")

    // If you haven't already you need to add the Dagger dependencies
    implementation("com.google.dagger:hilt-android:{{gradle.daggerVersion}}")
    kapt("com.google.dagger:hilt-android-compiler:{{gradle.daggerVersion}}")
}
```

### Kotlin Modules
For pure Kotlin modules you need to add KAPT, the auto-dagger core dependency, the auto-dagger compiler as well as the
hilt dependencies:
```kotlin
plugins {
    kotlin("kapt") // For Groovy build scripts use id('kotlin-kapt')
}

dependencies {
    implementation("se.ansman.dagger.auto:core:{{gradle.version}}")
    kapt("se.ansman.dagger.auto:compiler:{{gradle.version}}")
    
    // If you haven't already you need to add the Dagger dependencies
    implementation("com.google.dagger:hilt-core:{{gradle.daggerVersion}}")
    kapt("com.google.dagger:hilt-compiler:{{gradle.daggerVersion}}")
}
```

### Snapshots
Snapshots are published on every commit to [Sonatype's snapshot repository](https://s01.oss.sonatype.org/content/repositories/snapshots/se/ansman/dagger/auto/). 
To use a snapshot add the snapshot repository:
```kotlin
buildscripts {
    repositories {
        ...
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    implementation("se.ansman.dagger.auto:android:{{gradle.snapshotVersion}}")
    kapt("se.ansman.dagger.auto:compiler:{{gradle.snapshotVersion}}")
}
```