# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This is a reference project demonstrating AGP 9 best practices for Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) apps. It serves as a migration example showing the clean three-module structure required by AGP 9.

## Build Commands

```shell
# Build Android APK
./gradlew :androidApp:assembleDebug

# Run common tests
./gradlew :composeApp:commonTest

# Build KMP shared library (all targets)
./gradlew :composeApp:assemble

# Build iOS framework (for Xcode)
./gradlew :composeApp:assembleReleaseXCFramework
```

For iOS, open `iosApp/` in Xcode (requires Xcode 16+) and run from there. Requires the KMP IntelliJ/Android Studio plugin for IDE run configurations.

## Architecture

### Three-Module Structure (AGP 9 Requirement)

AGP 9 prohibits `com.android.application` and `kotlin.multiplatform` in the same Gradle module.

| Module | Plugin | Role |
|--------|--------|------|
| `androidApp` | `com.android.application` | Android entry point — `MainActivity`, `AndroidManifest.xml`, launcher icons |
| `composeApp` | `com.android.kotlin.multiplatform.library` | Shared KMP library — all UI (`App.kt`), business logic, shared resources |
| `iosApp` | Xcode project | iOS entry point — wraps `ComposeApp` framework via `MainViewController` |

`androidApp` depends on `composeApp` via `implementation(projects.composeApp)`.

### Key AGP 9 Patterns

- **`composeApp/build.gradle.kts`**: Android configuration lives inside `kotlin { androidLibrary {} }` — no top-level `android {}` block, no top-level `dependencies {}` block.
- **`androidApp/build.gradle.kts`**: Uses only `com.android.application` — the `kotlin.android` plugin is intentionally absent (AGP 9 built-in Kotlin).
- **`gradle.properties`**: No deprecated suppression flags (no `android.builtInKotlin=false`, no `android.newDsl=false`, etc.).

### Source Set Layout (`composeApp`)

```
composeApp/src/
  commonMain/kotlin/    # App.kt, Greeting.kt, Platform.kt (expect)
  commonMain/composeResources/  # Shared resources (strings, drawables)
  androidMain/kotlin/   # Platform.android.kt (actual)
  iosMain/kotlin/       # Platform.ios.kt (actual), MainViewController.kt
  commonTest/kotlin/    # ComposeAppCommonTest.kt
```

### Package

All Kotlin source uses `jp.jiho.cmpprofiledemo`.

## Key Versions

| Tool | Version |
|------|---------|
| AGP | 9.0.1 |
| Kotlin | 2.3.10 |
| Compose Multiplatform | 1.10.1 |
| Compile/Target SDK | 36 |
| Min SDK | 28 |
| JDK (required) | 17+ |
| Android Studio (required) | Otter 3 Feature Drop 2025.2.3+ |
| IntelliJ IDEA (required) | Q1 2026+ |
