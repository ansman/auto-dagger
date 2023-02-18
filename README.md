Auto Dagger [![Build Gradle](https://github.com/ansman/auto-dagger/actions/workflows/gradle.yml/badge.svg)](https://github.com/ansman/auto-dagger/actions/workflows/gradle.yml) [![Maven Central](https://img.shields.io/maven-central/v/se.ansman.dagger.auto/core.svg)](https://central.sonatype.dev/namespace/se.ansman.dagger.auto)
===
Auto Dagger allows you to automate some setup with Dagger and Hilt.

For example you can annotate `@Singleton` scoped objects with `@AutoInitialize` and have the be initialized during app
launch. 

This is done using Hilt and AndroidX Startup.

To read more please refer to the [documentation](https://auto-dagger.ansman.se/).

For the changelog see the [releases page](https://github.com/ansman/auto-dagger/releases).

Setup
---
```groovy
dependencies {
    // Include this in java or android modules
    implementation("se.ansman.dagger.auto:core:0.1.0")
    
    // Include this only in android modules
    implementation("se.ansman.dagger.auto:android:0.1.0")
    
    // If using Java
    annotationProcessor("se.ansman.dagger.auto:compiler:0.1.0")
    
    // If using Kotlin
    kapt("se.ansman.dagger.auto:compiler:0.1.0")
}
```

Basic usage
```kotlin
@AutoInitialize
@Singleton
class SomeRepository @Inject constructor() {
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

For the full documentation see

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
