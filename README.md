# Agp9Template

A Kotlin Multiplatform project targeting Android and iOS, structured to comply with Android Gradle Plugin 9 requirements.

## Module Structure

| Module | Plugin | Purpose |
|---|---|---|
| `androidApp` | `com.android.application` | Android entry point (APK / AAB) |
| `composeApp` | `com.android.kotlin.multiplatform.library` | Shared KMP library (UI, business logic) |
| `iosApp` | — (Xcode project) | iOS entry point |

### Why three modules?

AGP 9 enforces a strict separation: `com.android.application` and `kotlin.multiplatform` cannot coexist in the same Gradle module. The shared Compose Multiplatform code lives in `composeApp` as a KMP library; `androidApp` depends on it and owns the Android application manifest and entry point.

## AGP 9 Best Practices Demonstrated

- **`com.android.kotlin.multiplatform.library`** — the purpose-built AGP 9 plugin for modules that combine an Android library target with Kotlin Multiplatform targets.
- **`androidLibrary {}` inside `kotlin {}`** — Android configuration is nested within the `kotlin {}` block in `composeApp/build.gradle.kts`; there is no separate top-level `android {}` block.
- **AGP 9 built-in Kotlin** — `androidApp/build.gradle.kts` applies only `com.android.application`; the `kotlin.android` plugin is not needed and is not applied.
- **Clean `gradle.properties`** — no deprecated suppression flags (e.g. `android.suppressUnsupportedOptionWarnings`, `android.enableJetifier`).

## Requirements

| Tool | Minimum version |
|---|---|
| Android Studio | Otter 3 Feature Drop 2025.2.3 |
| IntelliJ IDEA | Q1 2026 |
| Xcode | 16+ |
| JDK | 17+ |

## Build and Run

### Android

Use the run configuration in your IDE toolbar, or run from the terminal:

macOS / Linux:
```shell
./gradlew :androidApp:assembleDebug
```

Windows:
```shell
.\gradlew.bat :androidApp:assembleDebug
```

### iOS

Open the [iosApp](./iosApp) directory in Xcode and run the scheme from there, or use the run configuration in your IDE toolbar if you have the Kotlin Multiplatform plugin installed.

## Source Layout

```
composeApp/
  src/
    commonMain/kotlin/   # Shared code for all targets
    androidMain/kotlin/  # Android-specific Kotlin code
    iosMain/kotlin/      # iOS-specific Kotlin code
androidApp/
  src/main/              # Android application manifest and entry point
iosApp/                  # Xcode project
```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and [Android Gradle Plugin 9](https://developer.android.com/build/releases/gradle-plugin).
