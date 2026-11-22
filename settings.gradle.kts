rootProject.name = "revanced-instagram-adblock"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/revanced/revanced-patches-gradle-plugin")
            credentials {
                username = providers.gradleProperty("githubPackagesUsername").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("githubPackagesPassword").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
        maven { url = uri("https://jitpack.io") }
    }
}

plugins {
    id("app.revanced.patches") version "1.0.0-dev.10"
}

settings {
    patchesProjectPath = "patches"
    extensions {
        projectsPath = null
    }
}