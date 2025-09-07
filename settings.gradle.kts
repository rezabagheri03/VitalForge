pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add additional repositories for specific dependencies
        maven("https://jitpack.io")
    }
}

rootProject.name = "VitalForge"
include(":app", ":companion")
