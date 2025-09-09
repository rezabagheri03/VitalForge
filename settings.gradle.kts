pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.google.com") }
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    plugins {
        id("com.android.application") version "8.7.2"
        id("org.jetbrains.kotlin.android") version "1.9.22"
        kotlin("kapt") version "1.9.22"
        id("com.google.dagger.hilt.android") version "2.48.1"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://maven.google.com") }
        mavenCentral()
        maven("https://jitpack.io")
    }
}
rootProject.name = "VitalForge"
include(":app", ":companion")
