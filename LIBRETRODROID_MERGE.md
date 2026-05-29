# LibretroDroid — Merged as Local Module

LibretroDroid (https://github.com/Swordfish90/LibretroDroid) has been merged
directly into this project as a local Gradle module instead of a JitPack dependency.

## Changes made

### New module: `:libretrodroid`
- Directory: `libretrodroid/`
- Contains all C++ sources (JNI layer, audio, video, input, renderers, VFS, Oboe)
- `build.gradle.kts` converted from Groovy to Kotlin DSL
- SDK versions aligned with lemuroid (compileSdk 35, minSdk 23, targetSdk 35, JVM 17)

### `settings.gradle.kts`
- Added `":libretrodroid"` to the `include(...)` block

### `lemuroid-app/build.gradle.kts`
- Replaced `implementation(deps.libs.libretrodroid)` →
  `implementation(project(":libretrodroid"))`

### `buildSrc/src/main/java/deps.kt`
- JitPack libretrodroid entry commented out (no longer used)

## Benefits
- No JitPack round-trip; faster and reproducible builds
- Full C++ source available for local edits (renderers, audio, input)
- Single-repo history and CI/CD
