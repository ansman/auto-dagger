Auto Dagger [![Build Gradle](https://github.com/ansman/auto-dagger/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/ansman/auto-dagger/actions/workflows/gradle.yml) [![Maven Central](https://img.shields.io/maven-central/v/se.ansman.dagger.auto/core.svg)](https://central.sonatype.com/search?namespace=se.ansman.dagger.auto)
===
Auto Dagger allows you to automate some Dagger setup using Hilt.

For example you can use the `@AutoInitialize` annotation to automatically initialize objects
during app launch and you can use the `@AutoBind` annotation to automatically bind objects.

Automatic initialization is done using AndroidX Startup.

To read more please refer to the [documentation](https://auto-dagger.ansman.se/).

For the changelog see the [releases page](https://github.com/ansman/auto-dagger/releases).

Basic usage
---
```kotlin
interface Repository

@AutoInitialize // Automatically create the repo during startup
@AutoBind // Automatically bind RealRepository as Repository
@Singleton
class RealRepository @Inject constructor() : Repository {
    init {
        // This will be executed at application startup, even if nobody injects it.
    }
}

@AutoInitialize
@Singleton
class InitializableRepository @Inject constructor() : Initializable {
    override fun initialize() {
        // This will be executed at application startup, even if nobody injects it.
    }
}

```

For the full documentation see https://auto-dagger.ansman.se/

Setup
---
For detailed instructions see the [getting-started](https://auto-dagger.ansman.se/latest/getting-started/) page.
```groovy
dependencies {
    // Set up your dagger dependencies and compiler
    
    // Include this in kotlin or android modules
    implementation("se.ansman.dagger.auto:core:0.8.0")
    kapt("se.ansman.dagger.auto:compiler:0.8.0")
    // If you're using KSP
    ksp("se.ansman.dagger.auto:compiler:0.8.0")
    

    // Include this only in android modules
    implementation("se.ansman.dagger.auto:android:0.8.0")
    
    // Add these if you want to replace objects during tests
    testImplementation("se.ansman.dagger.auto:android-testing:0.8.0")
    kaptTest("se.ansman.dagger.auto:compiler:0.8.0")
    // If you're using KSP
    kspTest("se.ansman.dagger.auto:compiler:0.8.0")
    
    // If you want to provide Retrofit services add the Retrofit dependency
    implementation("se.ansman.dagger.auto:retrofit:0.8.0")
    
    // If you want to inject a CoroutineScope into ViewModels add the ViewModel dependency
    implementation("se.ansman.dagger.auto:androidx-viewmodel:0.8.0")
    
    // If you want to automatically provide your Room DAOs add the Room dependency
    implementation("se.ansman.dagger.auto:androidx-room:0.8.0")
}
```

License
---
This project is licensed under the Apache-2.0 license. See [LICENSE.txt](LICENSE.txt) for the full license.
```plain

Copyright 2022-2023 Nicklas Ansman Giertz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
