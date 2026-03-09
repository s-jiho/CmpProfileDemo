# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This is a reference project demonstrating AGP 9 best practices for Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) apps. It serves as a migration example showing the clean three-module structure required by AGP 9.

## Build Commands

```shell
# Build Android APK
./gradlew :androidApp:assembleDebug

# Build Android APK for a specific flavor (dev/staging/production)
./gradlew :androidApp:assembleDebug -Pbuildkonfig.flavor=staging

# Run common tests
./gradlew :composeApp:commonTest

# Run a single test class
./gradlew :composeApp:iosSimulatorArm64Test --tests "jp.jiho.cmpprofiledemo.presentation.profile.ProfileViewModelTest"

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
| `composeApp` | `com.android.kotlin.multiplatform.library` | Shared KMP library — all UI, business logic, shared resources |
| `iosApp` | Xcode project | iOS entry point — wraps `ComposeApp` framework via `MainViewController` |

`androidApp` depends on `composeApp` via `implementation(projects.composeApp)`.

### Key AGP 9 Patterns

- **`composeApp/build.gradle.kts`**: Android configuration lives inside `kotlin { androidLibrary {} }` — no top-level `android {}` block, no top-level `dependencies {}` block.
- **`androidApp/build.gradle.kts`**: Uses only `com.android.application` — the `kotlin.android` plugin is intentionally absent (AGP 9 built-in Kotlin).
- **`gradle.properties`**: No deprecated suppression flags (no `android.builtInKotlin=false`, no `android.newDsl=false`, etc.).

### Layer Architecture (`composeApp/src/commonMain`)

```
jp.jiho.cmpprofiledemo/
  domain/
    model/          # Pure data classes (Profile)
    repository/     # Repository interfaces (ProfileRepository)
    AppError.kt     # Sealed class for domain errors
  data/
    dto/            # Ktor JSON DTOs (ProfileResponse, UpdateProfileRequest)
    network/        # ApiClient, HttpClientProvider, SerialApiExecutor, ErrorMapping
    repository/     # ProfileRepositoryImpl, MockProfileRepository
  presentation/
    profile/        # ProfileViewModel, ProfileUiState, ProfileEvent, ProfileStateReducer
    editprofile/    # EditProfileViewModel, UiState, Event, StateReducer, EditProfileValidator, EditProfileValidationError
    validation/     # ValidationErrorMessage (string resource lookups for Konform validation errors)
  ui/
    profile/        # ProfileScreen.kt
    editprofile/    # EditProfileScreen.kt
    navigation/     # AppNavGraph.kt, Routes.kt (type-safe @Serializable routes)
    common/         # AppErrorExt.kt
  di/
    AppModule.kt    # Koin modules: networkModule, repositoryModule, viewModelModule
    KoinInit.kt     # commonMain startKoin entry point
```

iOS targets: `iosArm64` and `iosSimulatorArm64` only (no x64).

Platform-specific sources:
- `androidMain/network/OfflineDetection.android.kt` — OkHttp-based
- `iosMain/network/OfflineDetection.ios.kt` — NWPathMonitor-based
- `iosMain/di/KoinInit.kt` — iOS Koin bootstrap

### State Management Pattern

Each screen follows a strict Event/State/Reducer/ViewModel pattern (all in `commonMain`):
- **`*UiState`** — immutable data class
- **`*Event`** — sealed class of all possible state transitions
- **`*StateReducer`** — pure object with `reduce(state, event): State` (unit-testable without coroutines)
- **`*ViewModel`** — holds `MutableStateFlow<*UiState>`, calls reducer via `update { reduce(it, event) }`, uses `onStart` + `stateIn(WhileSubscribed(5000))`

### Network Layer

`ApiClient` wraps Ktor `HttpClient` and serializes all requests through a `SerialApiExecutor` (channel-based queue, capacity 64). This prevents race conditions on mutation endpoints. Parallel requests are possible via `ApiClient.parallel {}` which holds the channel slot open for the entire parallel block.

### DI (Koin 4)

Modules in `AppModule.kt`: `networkModule` (singleton `HttpClientProvider` with `onClose`), `repositoryModule`, `viewModelModule` (using `viewModelOf`). Android starts Koin in `CmpProfileDemoApplication`; iOS starts it in `iosMain/di/KoinInit.kt`.

### BuildKonfig Flavors

`BASE_URL` is injected at build time via BuildKonfig. Flavors: `dev` (beeceptor mock), `staging`, `production`. Default (no flavor) falls back to `dev`.

### Package

All Kotlin source uses `jp.jiho.cmpprofiledemo`.

## Key Versions

| Tool | Version |
|------|---------|
| AGP | 9.0.1 |
| Kotlin | 2.3.10 |
| Compose Multiplatform | 1.10.1 |
| Ktor | 3.4.0 |
| Coil | 3.1.0 |
| Konform | 0.11.1 |
| Koin | 4.1.1 |
| Compile/Target SDK | 36 |
| Min SDK | 28 |
| JDK (required) | 17+ |
| Android Studio (required) | Otter 3 Feature Drop 2025.2.3+ |
| IntelliJ IDEA (required) | Q1 2026+ |

## Testing

Tests live in `composeApp/src/commonTest`. Libraries: kotlin-test, Kotest assertions, Turbine (Flow testing), kotlinx-coroutines-test, koin-test, ktor-client-mock. `MockProfileRepository` is the standard test double for repository layer.
