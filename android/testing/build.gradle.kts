plugins {
    kotlin("jvm")
    id("library")
    id("library.publishing")
}

dependencies {
    api(projects.core)
}