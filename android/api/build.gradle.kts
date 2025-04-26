plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
    id("library.api")
    id("library.publishing")
}

android {
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    api(projects.core)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.android)
    compileOnly(libs.androidx.startup)
    ksp(libs.dagger.compiler)
    ksp(libs.dagger.hilt.compiler)
}