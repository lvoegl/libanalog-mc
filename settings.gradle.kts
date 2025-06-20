pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.4.6"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        versions("1.20.1", "1.20.4", "1.20.6", "1.21.1", "1.21.3", "1.21.4", "1.21.5")
    }
    create(rootProject)
}

rootProject.name = "LibAnalog"