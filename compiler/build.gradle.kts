plugins {
    id("library.compiler")
    id("library.publishing")
    `java-test-fixtures`
}

dependencies {
    implementation(projects.core)
    implementation(projects.thirdParty.retrofit)
    implementation(projects.thirdParty.androidx.room)
    implementation(projects.thirdParty.android.testing)
    testFixturesApi(projects.compiler.common.testUtils)
}