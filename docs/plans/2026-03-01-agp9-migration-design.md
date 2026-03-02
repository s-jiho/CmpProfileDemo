# AGP 9 Migration Design — Agp9Template

Date: 2026-03-01
Author: Design via brainstorming session

## Goal

Convert this Compose Multiplatform template into a clean AGP 9 best-practices example. The result should serve as a reference for developers migrating their own CMP projects to AGP 9.

## Current State

| Item | Value |
|------|-------|
| AGP | 9.0.1 (version already updated) |
| Gradle | 9.2.1 |
| Kotlin | 2.3.10 |
| Compose Multiplatform | 1.10.1 |
| Problem | `composeApp` combines `kotlinMultiplatform` + `com.android.application` — incompatible in AGP 9 |
| Workaround in place | Multiple deprecated flags in `gradle.properties` suppressing AGP 9 errors |

## Target Architecture

### Module Structure

```
Agp9Template/
├── androidApp/          # NEW — Android entry point only
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/jp/jiho/agp9template/
│       │   └── MainActivity.kt
│       └── res/         # all mipmap, drawable, values assets
│
├── composeApp/          # CONVERTED — KMP shared library
│   ├── build.gradle.kts
│   └── src/
│       ├── androidMain/ # Platform.android.kt only
│       ├── commonMain/  # App.kt, Greeting.kt, Platform.kt, resources
│       ├── commonTest/
│       └── iosMain/
│
└── iosApp/              # unchanged
```

**Responsibility split:**
- `androidApp` — Android process entry point (Activity, Manifest, launcher icons)
- `composeApp` — All shared UI and business logic across platforms

---

## Step-by-Step Implementation Plan

### Step 1 — Create `androidApp` module

**`settings.gradle.kts`** — add include:
```kotlin
include(":composeApp")
include(":androidApp")
```

**`androidApp/build.gradle.kts`** — new file:
```kotlin
plugins {
    alias(libs.plugins.androidApplication)   // No kotlin.android — AGP 9 built-in Kotlin
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "jp.jiho.agp9template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "jp.jiho.agp9template"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}
```

**Files to move from `composeApp/src/androidMain/` to `androidApp/src/main/`:**
- `AndroidManifest.xml`
- `kotlin/jp/jiho/agp9template/MainActivity.kt`
- `res/` (all drawable, mipmap, values assets)

---

### Step 2 — Convert `composeApp` to KMP library

**`gradle/libs.versions.toml`** — add plugin alias:
```toml
[plugins]
androidMultiplatformLibrary = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
```

**`composeApp/build.gradle.kts`** — full replacement:
```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)  // replaces androidApplication
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {                                  // replaces androidTarget{}
        namespace = "jp.jiho.agp9template"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        androidResources {
            enable = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            // androidx.activity.compose moved to androidApp
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
// No top-level android{} block — moved into kotlin { androidLibrary {} }
// No top-level dependencies{} block — moved into kotlin { sourceSets {} }
```

---

### Step 3 — Clean up `gradle.properties`

**Remove all deprecated flags, keep only:**
```properties
#Kotlin
kotlin.code.style=official
kotlin.daemon.jvmargs=-Xmx3072M

#Gradle
org.gradle.jvmargs=-Xmx4096M -Dfile.encoding=UTF-8
org.gradle.configuration-cache=true
org.gradle.caching=true

#Android
android.nonTransitiveRClass=true
android.useAndroidX=true
android.defaults.buildfeatures.resvalues=true
android.sdk.defaultTargetSdkToCompileSdkIfUnset=false
```

**Removed flags and why:**

| Flag | Reason |
|------|--------|
| `android.newDsl=false` | AGP 9 uses new DSL by default — flag obsolete |
| `android.builtInKotlin=false` | Enabling built-in Kotlin is an AGP 9 best practice |
| `android.enableAppCompileTimeRClass=false` | Deprecated in AGP 9 |
| `android.r8.strictFullModeForKeepRules=false` | Deprecated in AGP 9 |
| `android.r8.optimizedResourceShrinking=false` | Deprecated in AGP 9 |
| `android.usesSdkInManifest.disallowed=false` | Deprecated in AGP 9 |
| `android.uniquePackageNames=false` | Deprecated in AGP 9 |
| `android.dependency.useConstraints=true` | Deprecated in AGP 9 |

---

## Key AGP 9 Best Practices Demonstrated

1. **Built-in Kotlin** — `com.android.application` without `kotlin.android` plugin
2. **`com.android.kotlin.multiplatform.library`** — new dedicated plugin for KMP+Android
3. **`androidLibrary {}` inside `kotlin {}`** — configuration co-located with KMP targets
4. **Clean `gradle.properties`** — no deprecated suppression flags

## IDE Requirements

| IDE | Minimum Version |
|-----|----------------|
| Android Studio | Otter 3 Feature Drop 2025.2.3+ |
| IntelliJ IDEA | Q1 2026 release |

## Reference

- https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html
