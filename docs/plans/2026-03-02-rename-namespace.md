# Rename Namespace Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** namespace / applicationId / Kotlin package を `jp.jiho.agp9template` から `jp.jiho.cmpprofiledemo` に一括変更する。

**Architecture:** ビルド設定 → Kotlin ソース移動 → iOS 設定 → ビルド検証の順で実施する。各ステップは独立して検証可能。

**Tech Stack:** Kotlin Multiplatform, AGP 9, Compose Multiplatform, Xcode (iOS)

---

### Task 1: androidApp/build.gradle.kts を更新

**Files:**
- Modify: `androidApp/build.gradle.kts:8,12`

**Step 1: namespace を変更**

```kotlin
// 変更前
namespace = "jp.jiho.agp9template"

// 変更後
namespace = "jp.jiho.cmpprofiledemo"
```

**Step 2: applicationId を変更**

```kotlin
// 変更前
applicationId = "jp.jiho.agp9template"

// 変更後
applicationId = "jp.jiho.cmpprofiledemo"
```

**Step 3: 構文確認**

```bash
./gradlew :androidApp:dependencies --configuration debugRuntimeClasspath 2>&1 | head -20
```
Expected: エラーなし（依存関係ツリーが表示される）

---

### Task 2: composeApp/build.gradle.kts を更新

**Files:**
- Modify: `composeApp/build.gradle.kts:12`

**Step 1: namespace を変更**

```kotlin
// 変更前
namespace = "jp.jiho.agp9template.shared"

// 変更後
namespace = "jp.jiho.cmpprofiledemo.shared"
```

**Step 2: 構文確認**

```bash
./gradlew :composeApp:dependencies --configuration commonMainImplementation 2>&1 | head -20
```
Expected: エラーなし

---

### Task 3: Kotlin ソースファイルを新パッケージディレクトリへ移動

**Files:**
- Move: `androidApp/src/main/kotlin/jp/jiho/agp9template/` → `androidApp/src/main/kotlin/jp/jiho/cmpprofiledemo/`
- Move: `composeApp/src/commonMain/kotlin/jp/jiho/agp9template/` → `composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/`
- Move: `composeApp/src/androidMain/kotlin/jp/jiho/agp9template/` → `composeApp/src/androidMain/kotlin/jp/jiho/cmpprofiledemo/`
- Move: `composeApp/src/iosMain/kotlin/jp/jiho/agp9template/` → `composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo/`
- Move: `composeApp/src/commonTest/kotlin/jp/jiho/agp9template/` → `composeApp/src/commonTest/kotlin/jp/jiho/cmpprofiledemo/`

**Step 1: 新ディレクトリを作成し git mv で移動（git 履歴を保持）**

```bash
# androidApp
mkdir -p androidApp/src/main/kotlin/jp/jiho/cmpprofiledemo
git mv androidApp/src/main/kotlin/jp/jiho/agp9template/MainActivity.kt \
        androidApp/src/main/kotlin/jp/jiho/cmpprofiledemo/MainActivity.kt

# composeApp commonMain
mkdir -p composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo
git mv composeApp/src/commonMain/kotlin/jp/jiho/agp9template/App.kt \
        composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/App.kt
git mv composeApp/src/commonMain/kotlin/jp/jiho/agp9template/Greeting.kt \
        composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/Greeting.kt
git mv composeApp/src/commonMain/kotlin/jp/jiho/agp9template/Platform.kt \
        composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/Platform.kt

# composeApp androidMain
mkdir -p composeApp/src/androidMain/kotlin/jp/jiho/cmpprofiledemo
git mv composeApp/src/androidMain/kotlin/jp/jiho/agp9template/Platform.android.kt \
        composeApp/src/androidMain/kotlin/jp/jiho/cmpprofiledemo/Platform.android.kt

# composeApp iosMain
mkdir -p composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo
git mv composeApp/src/iosMain/kotlin/jp/jiho/agp9template/MainViewController.kt \
        composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo/MainViewController.kt
git mv composeApp/src/iosMain/kotlin/jp/jiho/agp9template/Platform.ios.kt \
        composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo/Platform.ios.kt

# composeApp commonTest
mkdir -p composeApp/src/commonTest/kotlin/jp/jiho/cmpprofiledemo
git mv composeApp/src/commonTest/kotlin/jp/jiho/agp9template/ComposeAppCommonTest.kt \
        composeApp/src/commonTest/kotlin/jp/jiho/cmpprofiledemo/ComposeAppCommonTest.kt
```

**Step 2: 旧空ディレクトリを削除**

```bash
rmdir androidApp/src/main/kotlin/jp/jiho/agp9template
rmdir composeApp/src/commonMain/kotlin/jp/jiho/agp9template
rmdir composeApp/src/androidMain/kotlin/jp/jiho/agp9template
rmdir composeApp/src/iosMain/kotlin/jp/jiho/agp9template
rmdir composeApp/src/commonTest/kotlin/jp/jiho/agp9template
```

---

### Task 4: 各 Kotlin ファイルの package 宣言を更新

**Files（移動後のパス）:**
- Modify: `androidApp/src/main/kotlin/jp/jiho/cmpprofiledemo/MainActivity.kt:1`
- Modify: `composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/App.kt:1`
- Modify: `composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/Greeting.kt:1`
- Modify: `composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/Platform.kt:1`
- Modify: `composeApp/src/androidMain/kotlin/jp/jiho/cmpprofiledemo/Platform.android.kt:1`
- Modify: `composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo/MainViewController.kt:1`
- Modify: `composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo/Platform.ios.kt:1`
- Modify: `composeApp/src/commonTest/kotlin/jp/jiho/cmpprofiledemo/ComposeAppCommonTest.kt:1`

各ファイルの先頭行を以下のように変更する：

```kotlin
// 変更前
package jp.jiho.agp9template

// 変更後
package jp.jiho.cmpprofiledemo
```

**Step 1: 全ファイルの package 宣言を一括確認**

```bash
grep -r "^package jp.jiho" composeApp/src androidApp/src
```
Expected: `jp.jiho.cmpprofiledemo` のみが表示される

---

### Task 5: iOS 設定を更新

**Files:**
- Modify: `iosApp/Configuration/Config.xcconfig:4`

**Step 1: Bundle Identifier を変更**

```
# 変更前
PRODUCT_BUNDLE_IDENTIFIER=jp.jiho.agp9template.Agp9Template$(TEAM_ID)

# 変更後
PRODUCT_BUNDLE_IDENTIFIER=jp.jiho.cmpprofiledemo.CmpProfileDemo$(TEAM_ID)
```

---

### Task 6: CLAUDE.md のドキュメントを更新

**Files:**
- Modify: `CLAUDE.md`（Package セクション）

**Step 1: パッケージ名の記述を更新**

```markdown
# 変更前
All Kotlin source uses `jp.jiho.agp9template`.

# 変更後
All Kotlin source uses `jp.jiho.cmpprofiledemo`.
```

---

### Task 7: ビルド検証

**Step 1: Android ビルドを実行**

```bash
./gradlew :androidApp:assembleDebug
```
Expected: `BUILD SUCCESSFUL`

**Step 2: 共通テストを実行**

```bash
./gradlew :composeApp:commonTest
```
Expected: `BUILD SUCCESSFUL` / テスト全通過

**Step 3: 残存する旧 namespace の確認**

```bash
grep -r "jp.jiho.agp9template" androidApp/src composeApp/src iosApp/Configuration
```
Expected: 出力なし（0 件）

---

### Task 8: コミット

**Step 1: 変更をステージング**

```bash
git add androidApp/build.gradle.kts \
        composeApp/build.gradle.kts \
        androidApp/src/ \
        composeApp/src/ \
        iosApp/Configuration/Config.xcconfig \
        CLAUDE.md
```

**Step 2: コミット**

```bash
git commit -m "refactor: rename namespace from agp9template to cmpprofiledemo"
```

---

## 変更ファイル一覧まとめ

| ファイル | 変更種別 |
|---|---|
| `androidApp/build.gradle.kts` | namespace, applicationId 変更 |
| `composeApp/build.gradle.kts` | namespace 変更 |
| `androidApp/src/main/kotlin/jp/jiho/cmpprofiledemo/MainActivity.kt` | 移動 + package 変更 |
| `composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/App.kt` | 移動 + package 変更 |
| `composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/Greeting.kt` | 移動 + package 変更 |
| `composeApp/src/commonMain/kotlin/jp/jiho/cmpprofiledemo/Platform.kt` | 移動 + package 変更 |
| `composeApp/src/androidMain/kotlin/jp/jiho/cmpprofiledemo/Platform.android.kt` | 移動 + package 変更 |
| `composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo/MainViewController.kt` | 移動 + package 変更 |
| `composeApp/src/iosMain/kotlin/jp/jiho/cmpprofiledemo/Platform.ios.kt` | 移動 + package 変更 |
| `composeApp/src/commonTest/kotlin/jp/jiho/cmpprofiledemo/ComposeAppCommonTest.kt` | 移動 + package 変更 |
| `iosApp/Configuration/Config.xcconfig` | Bundle Identifier 変更 |
| `CLAUDE.md` | ドキュメント更新 |
