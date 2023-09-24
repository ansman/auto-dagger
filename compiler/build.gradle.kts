plugins {
    id("library.compiler")
    id("library.publishing")
    `java-test-fixtures`
}

dependencies {
    implementation(projects.core)
    implementation(projects.retrofit)
    implementation(projects.androidx.room)
    implementation(projects.android.testing)
    testFixturesApi(projects.compiler.common.testUtils)
}