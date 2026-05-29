@file:Suppress("ktlint")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

// Cores are downloaded at runtime from https://buildbot.libretro.com/nightly/android/latest/
// No dynamic feature modules or lemuroid-cores needed.
include(
    ":libretrodroid",
    ":retrograde-util",
    ":retrograde-app-shared",
    ":lemuroid-touchinput",
    ":lemuroid-app",
    ":lemuroid-metadata-libretro-db",
    ":lemuroid-app-ext-free",
    ":lemuroid-app-ext-play",
    ":baselineprofile"
)
