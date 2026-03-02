# AGP 9 Migration Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Migrate the Agp9Template Compose Multiplatform project to proper AGP 9 structure by separating the Android entry point into a dedicated `androidApp` module and converting `composeApp` to a KMP library.

**Architecture:** Create a new `androidApp` module that owns the Android process entry point (Activity, Manifest, launcher resources) using AGP 9's built-in Kotlin support. Convert the existing `composeApp` module from `com.android.application` to `com.android.kotlin.multiplatform.library` with `androidLibrary {}` configuration. Clean up all deprecated `gradle.properties` flags.

**Tech Stack:** Kotlin 2.3.10, Compose Multiplatform 1.10.1, AGP 9.0.1, Gradle 9.2.1

---

### Task 1: Clean up `gradle.properties`

**Files:**
- Modify: `gradle.properties`

**Step 1: Remove all deprecated flags**

Replace the entire file with:

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

**Step 2: Verify the file looks correct**

Open `gradle.properties` and confirm the 8 deprecated flags below are gone:
- `android.newDsl=false`
- `android.builtInKotlin=false`
- `android.enableAppCompileTimeRClass=false`
- `android.r8.strictFullModeForKeepRules=false`
- `android.r8.optimizedResourceShrinking=false`
- `android.usesSdkInManifest.disallowed=false`
- `android.uniquePackageNames=false`
- `android.dependency.useConstraints=true`

**Step 3: Commit**

```bash
git add gradle.properties
git commit -m "chore: remove deprecated AGP 9 suppression flags from gradle.properties"
```

---

### Task 2: Add `androidMultiplatformLibrary` plugin alias

**Files:**
- Modify: `gradle/libs.versions.toml`

**Step 1: Add plugin entry**

In `gradle/libs.versions.toml`, under `[plugins]`, add one line:

```toml
androidMultiplatformLibrary = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
```

The `[plugins]` section should look like this after the change:

```toml
[plugins]
androidApplication = { id = "com.android.application", version.ref = "agp" }
androidLibrary = { id = "com.android.library", version.ref = "agp" }
androidMultiplatformLibrary = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
composeMultiplatform = { id = "org.jetbrains.compose", version.ref = "composeMultiplatform" }
composeCompiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
```

**Step 2: Commit**

```bash
git add gradle/libs.versions.toml
git commit -m "chore: add androidMultiplatformLibrary plugin alias for AGP 9 KMP library"
```

---

### Task 3: Create `androidApp` module directory structure

**Files:**
- Create: `androidApp/src/main/kotlin/jp/jiho/agp9template/` (directory)
- Create: `androidApp/src/main/res/` (directory)

**Step 1: Create the directory tree**

```bash
mkdir -p androidApp/src/main/kotlin/jp/jiho/agp9template
mkdir -p androidApp/src/main/res
```

**Step 2: Create `androidApp/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.androidApplication)
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

Note: No `kotlin.android` plugin — AGP 9 has built-in Kotlin for `com.android.application`.

**Step 3: Register module in `settings.gradle.kts`**

Add `include(":androidApp")` so `settings.gradle.kts` becomes:

```kotlin
rootProject.name = "Agp9Template"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":androidApp")
```

**Step 4: Commit**

```bash
git add androidApp/build.gradle.kts settings.gradle.kts
git commit -m "feat: add androidApp module skeleton for AGP 9 Android entry point"
```

---

### Task 4: Move Android entry point files to `androidApp`

**Files:**
- Move: `composeApp/src/androidMain/AndroidManifest.xml` → `androidApp/src/main/AndroidManifest.xml`
- Move: `composeApp/src/androidMain/kotlin/jp/jiho/agp9template/MainActivity.kt` → `androidApp/src/main/kotlin/jp/jiho/agp9template/MainActivity.kt`
- Move: `composeApp/src/androidMain/res/` → `androidApp/src/main/res/`

**Step 1: Copy `AndroidManifest.xml`**

Create `androidApp/src/main/AndroidManifest.xml` with this content (identical to original):

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity
            android:exported="true"
            android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**Step 2: Copy `MainActivity.kt`**

Create `androidApp/src/main/kotlin/jp/jiho/agp9template/MainActivity.kt`:

```kotlin
package jp.jiho.agp9template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
```

**Step 3: Copy all `res/` files**

```bash
cp -r composeApp/src/androidMain/res/* androidApp/src/main/res/
```

Verify the following exist in `androidApp/src/main/res/`:
- `drawable-v24/ic_launcher_foreground.xml`
- `drawable/ic_launcher_background.xml`
- `mipmap-anydpi-v26/ic_launcher.xml`
- `mipmap-anydpi-v26/ic_launcher_round.xml`
- `mipmap-hdpi/`, `mipmap-mdpi/`, `mipmap-xhdpi/`, `mipmap-xxhdpi/`, `mipmap-xxxhdpi/` (PNG files)
- `values/strings.xml`

**Step 4: Commit**

```bash
git add androidApp/src/
git commit -m "feat: move Android entry point (MainActivity, Manifest, res) to androidApp"
```

---

### Task 5: Convert `composeApp` to KMP library

**Files:**
- Modify: `composeApp/build.gradle.kts`

**Step 1: Replace `composeApp/build.gradle.kts` entirely**

```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
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
```

Key changes from original:
- `androidApplication` → `androidMultiplatformLibrary`
- `androidTarget {}` → `androidLibrary {}` (inside `kotlin {}`)
- `namespace`, `compileSdk` moved into `androidLibrary {}`
- `compilerOptions` moved into `androidLibrary {}`
- Top-level `android {}` block removed
- Top-level `dependencies {}` block removed
- `androidx.activity.compose` removed from `androidMain` (now in `androidApp`)

**Step 2: Delete the original Android entry point files from `composeApp`**

```bash
rm composeApp/src/androidMain/AndroidManifest.xml
rm composeApp/src/androidMain/kotlin/jp/jiho/agp9template/MainActivity.kt
rm -rf composeApp/src/androidMain/res
```

After deletion, `composeApp/src/androidMain/` should contain only:
- `kotlin/jp/jiho/agp9template/Platform.android.kt`

**Step 3: Commit**

```bash
git add composeApp/build.gradle.kts
git rm composeApp/src/androidMain/AndroidManifest.xml
git rm composeApp/src/androidMain/kotlin/jp/jiho/agp9template/MainActivity.kt
git rm -r composeApp/src/androidMain/res
git commit -m "feat: convert composeApp to com.android.kotlin.multiplatform.library"
```

---

### Task 6: Verify the build

**Step 1: Run full build**

```bash
./gradlew build
```

Expected: `BUILD SUCCESSFUL`

If you see errors related to `androidLibrary` or plugin resolution, check:
- AGP version is 9.0.1 in `libs.versions.toml`
- `com.android.kotlin.multiplatform.library` is a valid plugin ID for AGP 9.0+

**Step 2: Run Android-specific assembly**

```bash
./gradlew :androidApp:assembleDebug
```

Expected: `BUILD SUCCESSFUL` with APK generated at `androidApp/build/outputs/apk/debug/androidApp-debug.apk`

**Step 3: Run common tests**

```bash
./gradlew :composeApp:allTests
```

Expected: `BUILD SUCCESSFUL`

**Step 4: Commit**

No new files to commit here. If fixes were needed in previous steps, they should have been committed with those tasks.

---

### Task 7: Update README

**Files:**
- Modify: `README.md`

**Step 1: Open `README.md` and add AGP 9 migration notes**

Add a section explaining:
- The new module structure (`androidApp` + `composeApp`)
- Why the split was needed (AGP 9 incompatibility)
- Key AGP 9 best practices demonstrated

**Step 2: Commit**

```bash
git add README.md
git commit -m "docs: document AGP 9 module structure in README"
```

---

## Summary of All Changes

| File | Action |
|------|--------|
| `gradle.properties` | Remove 8 deprecated flags |
| `gradle/libs.versions.toml` | Add `androidMultiplatformLibrary` plugin alias |
| `settings.gradle.kts` | Add `include(":androidApp")` |
| `androidApp/build.gradle.kts` | Create new file |
| `androidApp/src/main/AndroidManifest.xml` | Move from composeApp |
| `androidApp/src/main/kotlin/.../MainActivity.kt` | Move from composeApp |
| `androidApp/src/main/res/` | Move from composeApp |
| `composeApp/build.gradle.kts` | Replace — new plugin + `androidLibrary {}` DSL |
| `composeApp/src/androidMain/AndroidManifest.xml` | Delete |
| `composeApp/src/androidMain/kotlin/.../MainActivity.kt` | Delete |
| `composeApp/src/androidMain/res/` | Delete |
| `README.md` | Update with migration notes |
