plugins {
    id("library.compiler")
    id("library.publishing")
    `java-test-fixtures`
}

dependencies {
    implementation(projects.core)
    implementation(projects.android.testing)
    implementation(projects.thirdParty.retrofit)
    implementation(projects.thirdParty.ktorfit)
    implementation(projects.thirdParty.androidx.room)
    testFixturesApi(projects.compiler.common.testUtils)
    testImplementation(projects.compiler.common.testUtils)
}