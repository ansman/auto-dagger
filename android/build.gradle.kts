plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("library")
    id("library.publishing")
    id("com.google.dagger.hilt.android")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

androidComponents {
    beforeVariants(selector().withBuildType("release")) { variant ->
        with(variant) {
            enableAndroidTest = false
            enableTestFixtures = false
            enableUnitTest = false
        }
    }
}

dependencies {
    api(projects.core)
    implementation(libs.dagger)
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.startup)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.hilt.compiler)
    androidTestImplementation(libs.dagger.hilt.android.testing)
    kaptAndroidTest(libs.dagger.compiler)
    kaptAndroidTest(libs.dagger.hilt.compiler)
    kaptAndroidTest(projects.compiler)
}