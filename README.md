# CmpProfileDemo

A Kotlin Multiplatform reference project targeting Android and iOS, demonstrating AGP 9 best practices with a practical Profile feature — including a profile view screen and an edit profile screen with form validation.

## Features

- **Profile Screen** — displays user profile data fetched from a remote API
- **Edit Profile Screen** — form with field-level validation (name, email, bio, avatar URL, notification toggle)
- Offline detection (platform-specific: OkHttp `NetworkInterceptor` on Android, `NWPathMonitor` on iOS)
- Build-time environment switching via BuildKonfig flavors (`dev` / `staging` / `production`)

## Architecture

The app follows a layered architecture inside `composeApp` (the shared KMP module):

```
domain/       # Interfaces and pure models (ProfileRepository, Profile, AppError)
data/         # Ktor-based network, DTOs, and repository implementations
presentation/ # ViewModels + MVI-style state management (Event / UiState / Reducer)
ui/           # Composable screens and type-safe navigation
di/           # Koin module definitions
```

### State Management

Each screen uses an **Event → Reducer → UiState** cycle:

| File | Role |
|------|------|
| `*Event` | Sealed class enumerating all state transitions |
| `*UiState` | Immutable data class |
| `*StateReducer` | Pure `object` — no coroutines, fully unit-testable |
| `*ViewModel` | Owns the `StateFlow`, calls the reducer, coordinates I/O |

### Network

`ApiClient` serializes all HTTP calls through `SerialApiExecutor` (a coroutine channel with capacity 64) to prevent race conditions on mutation endpoints. Parallel requests are supported via `ApiClient.parallel {}`.

## Module Structure

AGP 9 prohibits applying both `com.android.application` and `kotlin.multiplatform` to the same Gradle module. This project uses the canonical three-module layout:

| Module | Gradle Plugin | Role |
|--------|---------------|------|
| `androidApp` | `com.android.application` | Android entry point — `MainActivity`, `AndroidManifest.xml`, launcher icons |
| `composeApp` | `com.android.kotlin.multiplatform.library` | Shared KMP library — all UI, business logic, resources |
| `iosApp` | Xcode project | iOS entry point — embeds `ComposeApp.framework` via `MainViewController` |

`androidApp` declares `implementation(projects.composeApp)` as its only project dependency.

## Libraries

### Core

| Library | Version | Purpose |
|---------|---------|---------|
| [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) | 2.3.10 | Shared code across Android and iOS |
| [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) | 1.10.1 | Shared UI framework |
| [AndroidX Lifecycle (KMP)](https://developer.android.com/jetpack/androidx/releases/lifecycle) | 2.9.6 | `ViewModel`, `collectAsStateWithLifecycle` |
| [Navigation Compose (KMP)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html) | 2.9.2 | Type-safe `@Serializable` route objects |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | 1.10.0 | JSON serialization for network DTOs |

### Networking

| Library | Version | Purpose |
|---------|---------|---------|
| [Ktor Client](https://ktor.io/docs/client-create-multiplatform-application.html) | 3.4.0 | Multiplatform HTTP client (`OkHttp` on Android, `Darwin` on iOS) |
| [Coil 3](https://coil-kt.github.io/coil/) | 3.1.0 | Async image loading (`coil3-compose`, `coil3-network-ktor3`) |

### Dependency Injection

| Library | Version | Purpose |
|---------|---------|---------|
| [Koin](https://insert-koin.io/) | 4.1.1 | DI framework (`koin-core`, `koin-compose`, `koin-compose-viewmodel`) |

### Validation

| Library | Version | Purpose |
|---------|---------|---------|
| [Konform](https://www.konform.io/) | 0.11.1 | Type-safe multiplatform form validation |

### Build Config

| Library | Version | Purpose |
|---------|---------|---------|
| [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) | 0.17.1 | Build-time constants per flavor (`BASE_URL`, etc.) |

### Testing

| Library | Version | Purpose |
|---------|---------|---------|
| [kotlin-test](https://kotlinlang.org/api/latest/kotlin.test/) | 2.3.10 | Multiplatform test framework |
| [Kotest Assertions](https://kotest.io/) | 5.9.1 | Fluent assertion DSL |
| [Turbine](https://github.com/cashapp/turbine) | 1.2.0 | `Flow` / `StateFlow` testing |
| [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines) | 1.10.2 | `TestScope`, `runTest` |
| [Ktor MockEngine](https://ktor.io/docs/client-testing.html) | 3.4.0 | In-process HTTP mock for repository tests |
| [koin-test](https://insert-koin.io/docs/reference/koin-test/testing/) | 4.1.1 | Koin module verification |

## Versions

| Tool / Library | Version |
|----------------|---------|
| **AGP** | **9.0.1** |
| Kotlin | 2.3.10 |
| Compose Multiplatform | 1.10.1 |
| Compile SDK / Target SDK | 36 |
| Min SDK | 28 |
| JDK (required) | 17+ |
| Android Studio | Otter 3 Feature Drop 2025.2.3+ |
| IntelliJ IDEA | Q1 2026+ |
| Xcode | 16+ |

## AGP 9 Best Practices

- **`com.android.kotlin.multiplatform.library`** — the plugin purpose-built for modules combining an Android library target with KMP targets.
- **`androidLibrary {}` inside `kotlin {}`** — `composeApp/build.gradle.kts` has no top-level `android {}` block; all Android config is nested inside `kotlin { androidLibrary { … } }`.
- **No `kotlin.android` in `androidApp`** — AGP 9 provides Kotlin support built-in; the `kotlin.android` plugin is intentionally absent.
- **Clean `gradle.properties`** — no deprecated suppression flags (`android.suppressUnsupportedOptionWarnings`, `android.enableJetifier`, `android.builtInKotlin=false`, etc.).

## Build and Run

### Android

```shell
# Debug build (default flavor = dev)
./gradlew :androidApp:assembleDebug

# Debug build with a specific flavor
./gradlew :androidApp:assembleDebug -Pbuildkonfig.flavor=staging
```

Install the generated APK from `androidApp/build/outputs/apk/debug/`.

### iOS

Open `iosApp/` in Xcode and run the scheme, or use the IDE run configuration (requires the [Kotlin Multiplatform plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform)).

To build the XCFramework manually:

```shell
./gradlew :composeApp:assembleReleaseXCFramework
```

### Tests

```shell
# All common tests (runs on iOS Simulator by default)
./gradlew :composeApp:commonTest

# Single test class
./gradlew :composeApp:iosSimulatorArm64Test \
  --tests "jp.jiho.cmpprofiledemo.presentation.profile.ProfileViewModelTest"
```

---

Learn more: [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) · [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) · [AGP 9 Release Notes](https://developer.android.com/build/releases/gradle-plugin)
