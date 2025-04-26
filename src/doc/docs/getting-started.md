# Getting Started

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/se.ansman.dagger.auto/core.svg)](https://central.sonatype.com/search?smo=true&q=se.ansman.dagger.auto)

### Android Modules
For pure Kotlin modules you need to add KSP, the auto-dagger android dependency, the 
auto-dagger compiler as well as the hilt dependencies:
```kotlin
plugins {
    id("com.google.devtools.ksp")
    
    id("com.google.dagger.hilt.android") // Optional, but recommended
}

dependencies {
    // For app modules add the android dependency
    implementation("se.ansman.dagger.auto:android:{{gradle.version}}")
    // And for library modules add the android-api dependency:
    implementation("se.ansman.dagger.auto:android-api:{{gradle.version}}")
    
    // Next add the compiler using KSP
    ksp("se.ansman.dagger.auto:compiler:{{gradle.version}}")

    // Add these if you want to replace objects during tests
    testImplementation("se.ansman.dagger.auto:android-testing:{{gradle.version}}")
    kspTest("se.ansman.dagger.auto:compiler:{{gradle.version}}")

    // If you haven't already, you need to add the Dagger dependencies
    implementation("com.google.dagger:hilt-android:{{gradle.daggerVersion}}")
    ksp("se.ansman.dagger.auto:compiler:{{gradle.version}}")
}
```

### Kotlin Modules
For pure Kotlin modules you need to add KSP, the auto-dagger core dependency, the auto-dagger compiler as well as the
hilt dependencies:
```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("se.ansman.dagger.auto:core:{{gradle.version}}")
    ksp("se.ansman.dagger.auto:compiler:{{gradle.version}}")
    
    // If you haven't already you need to add the Dagger dependencies
    implementation("com.google.dagger:hilt-core:{{gradle.daggerVersion}}")
    ksp("com.google.dagger:hilt-compiler:{{gradle.daggerVersion}}")
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
    ksp("se.ansman.dagger.auto:compiler:{{gradle.snapshotVersion}}")
}
```