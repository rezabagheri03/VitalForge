// No buildscript{} block needed when using pluginManagement{}

// Declare plugin versions for subprojects, but do not apply them here.
plugins {
    id("com.android.application") apply false
    id("org.jetbrains.kotlin.android") apply false
    kotlin("kapt") apply false
    id("com.google.dagger.hilt.android") apply false
}
