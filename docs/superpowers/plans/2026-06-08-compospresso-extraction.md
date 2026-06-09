# Compospresso Extraction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Extract the `com.life360.android.doppio` package from the L360 monorepo into a standalone Android library at `~/Workspace/ed-devtech/compospresso/`, renamed to Compospresso, with all L360 internal dependencies inlined.

**Architecture:** Single-module Gradle project (`:library`) with package `dev.eddev.compospresso`. Source files are bulk-copied from the L360 monorepo with package rewrites; three internal symbols (`TAG`, `TestError`, `TestLog`) are replaced with minimal in-package equivalents under `dev.eddev.compospresso.internal`.

**Tech Stack:** Kotlin 2.0.21, AGP 8.7.x, JDK 17, Espresso 3.6.1, UiAutomator 2.3.0, Compose UI Test (BoM 2024.10.01), Material 1.12.0, AppCompat 1.7.0.

**Source root (L360 monorepo):** `/Users/eduard/Downloads/android/testsupport/src/main/java/com/life360/android/doppio/`

**Target repo root:** `/Users/eduard/Workspace/ed-devtech/compospresso/` (already exists, contains only `docs/` + a committed design spec, `git init` already done on `main`)

---

## Task 1: Project scaffolding (gradle wrapper, settings, top-level build, version catalog)

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`
- Create: `gradle/libs.versions.toml`
- Create: `.gitignore`
- Create: `LICENSE`
- Run: `gradle wrapper` to generate `gradlew`, `gradlew.bat`, `gradle/wrapper/*`

- [ ] **Step 1: Create `.gitignore`**

```
.gradle/
build/
local.properties
.idea/
*.iml
.DS_Store
captures/
.externalNativeBuild/
.cxx/
```

- [ ] **Step 2: Create `LICENSE` (Apache-2.0)**

Use the standard Apache 2.0 text. Copyright line: `Copyright 2026 Eduard Dulmaganov`.

Download from `https://www.apache.org/licenses/LICENSE-2.0.txt` and replace the boilerplate with the copyright line above the license body.

- [ ] **Step 3: Create `settings.gradle.kts`**

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "compospresso"
include(":library")
```

- [ ] **Step 4: Create `gradle.properties`**

```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
android.nonTransitiveRClass=true
kotlin.code.style=official
```

- [ ] **Step 5: Create `gradle/libs.versions.toml`**

```toml
[versions]
agp = "8.7.3"
kotlin = "2.0.21"
appcompat = "1.7.0"
coordinatorlayout = "1.2.0"
material = "1.12.0"
espresso = "3.6.1"
uiautomator = "2.3.0"
testCore = "1.6.1"
composeBom = "2024.10.01"

[libraries]
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
androidx-coordinatorlayout = { group = "androidx.coordinatorlayout", name = "coordinatorlayout", version.ref = "coordinatorlayout" }
google-material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso" }
androidx-espresso-intents = { group = "androidx.test.espresso", name = "espresso-intents", version.ref = "espresso" }
androidx-uiautomator = { group = "androidx.test.uiautomator", name = "uiautomator", version.ref = "uiautomator" }
androidx-test-core = { group = "androidx.test", name = "core", version.ref = "testCore" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }

[plugins]
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

- [ ] **Step 6: Create top-level `build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}
```

- [ ] **Step 7: Generate gradle wrapper**

Run from `~/Workspace/ed-devtech/compospresso/`:

```bash
gradle wrapper --gradle-version 8.10.2 --distribution-type bin
```

Expected: `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties` are created. If system `gradle` is not installed, install via `brew install gradle` first.

- [ ] **Step 8: Verify wrapper resolves**

Run:

```bash
./gradlew --version
```

Expected: Prints Gradle 8.10.2, JVM 17.x, Kotlin 1.9.x (the wrapper's bundled kotlin, unrelated to project Kotlin), OS Mac OS X.

- [ ] **Step 9: Commit**

```bash
git add .gitignore LICENSE settings.gradle.kts build.gradle.kts gradle.properties gradle/ gradlew gradlew.bat
git commit -m "Bootstrap Gradle project for Compospresso"
```

---

## Task 2: `:library` module configuration

**Files:**
- Create: `library/build.gradle.kts`
- Create: `library/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create `library/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "dev.eddev.compospresso"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.androidx.appcompat)
    api(libs.androidx.coordinatorlayout)
    api(libs.google.material)
    api(libs.androidx.espresso.core)
    api(libs.androidx.espresso.intents)
    api(libs.androidx.uiautomator)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.test.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "dev.eddev"
            artifactId = "compospresso"
            version = "0.1.0"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
```

- [ ] **Step 2: Create `library/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

- [ ] **Step 3: Create empty `library/consumer-rules.pro`**

```
# No consumer rules at this time.
```

- [ ] **Step 4: Verify Gradle configuration resolves**

Run:

```bash
./gradlew :library:tasks --quiet
```

Expected: Prints task list including `assembleDebug`, `assembleRelease`, `publishToMavenLocal`. No configuration errors.

- [ ] **Step 5: Commit**

```bash
git add library/
git commit -m "Configure :library module (AGP, deps, maven-publish)"
```

---

## Task 3: Internal replacements for L360 dependencies

**Files:**
- Create: `library/src/main/java/dev/eddev/compospresso/internal/CompospressoError.kt`
- Create: `library/src/main/java/dev/eddev/compospresso/internal/CompospressoLog.kt`
- Create: `library/src/main/java/dev/eddev/compospresso/internal/Tag.kt`

These three files replace `com.life360.android.testsupport.{TestError, TestLog, TAG}` and `com.life360.android.doppio.doppioutils.DoppioError`. The original Doppio call sites use only `TestLog.i(tag, message)`, `TestError(tag, message)`, and the `Any.TAG` extension property (verified by grep on source).

- [ ] **Step 1: Create `Tag.kt`**

```kotlin
package dev.eddev.compospresso.internal

/**
 * Extension property to use a class's simple name as a log tag.
 * Truncated to 23 chars to satisfy Logcat's tag length limit on older platforms.
 * Use: CompospressoLog.i(TAG, "message")
 */
val Any.TAG: String
    get() {
        val tag = this.javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }
```

- [ ] **Step 2: Create `CompospressoError.kt`**

```kotlin
package dev.eddev.compospresso.internal

/**
 * Single error type for test-side failures raised by Compospresso interactions and assertions.
 * Replaces both Doppio's DoppioError and L360 testsupport's TestError.
 */
open class CompospressoError(tag: String, message: String) : Error("$tag: $message")
```

- [ ] **Step 3: Create `CompospressoLog.kt`**

```kotlin
package dev.eddev.compospresso.internal

import android.annotation.SuppressLint
import android.util.Log

/**
 * Minimal log facade used by Compospresso interactions and decorators.
 * Wraps android.util.Log. No file output, no metrics — pure logcat.
 */
@SuppressLint("WrongLogDetector")
object CompospressoLog {

    private const val LOG_TAG = "Compospresso"

    fun i(tag: String, message: String) {
        Log.i(LOG_TAG, "$tag: $message")
    }

    fun w(tag: String, message: String) {
        Log.w(LOG_TAG, "$tag: $message")
    }

    fun e(tag: String, message: String) {
        Log.e(LOG_TAG, "$tag: $message")
    }

    fun e(tag: String, message: String, tr: Throwable) {
        Log.e(LOG_TAG, "$tag: $message", tr)
    }
}
```

- [ ] **Step 4: Verify these three files compile**

Run:

```bash
./gradlew :library:compileDebugKotlin
```

Expected: BUILD SUCCESSFUL. Only these three Kotlin files exist under `src/main/java/` at this point; build will compile them and stop with no other errors.

- [ ] **Step 5: Commit**

```bash
git add library/src/main/java/dev/eddev/compospresso/internal/
git commit -m "Add internal replacements: CompospressoError, CompospressoLog, TAG"
```

---

## Task 4: Bulk-copy Doppio sources with package + import rewrites

**Files:**
- Create: ~20 source files under `library/src/main/java/dev/eddev/compospresso/` (mirroring Doppio's subdirectory structure, minus `doppioutils/`)
- Source: `/Users/eduard/Downloads/android/testsupport/src/main/java/com/life360/android/doppio/`

The Doppio source directory has 20 Kotlin files plus a README. The README is handled in Task 6; `doppioutils/DoppioError.kt` is replaced by `internal/CompospressoError.kt` (Task 3) and is NOT copied. All other files are copied with these in-file rewrites:

| Find | Replace |
|---|---|
| `com.life360.android.doppio.doppioutils.DoppioError` | `dev.eddev.compospresso.internal.CompospressoError` |
| `com.life360.android.doppio` | `dev.eddev.compospresso` |
| `com.life360.android.testsupport.TestError` | `dev.eddev.compospresso.internal.CompospressoError` |
| `com.life360.android.testsupport.TestLog` | `dev.eddev.compospresso.internal.CompospressoLog` |
| `com.life360.android.testsupport.TAG` | `dev.eddev.compospresso.internal.TAG` |
| `DoppioError(` | `CompospressoError(` |
| `TestError(` | `CompospressoError(` |
| `TestLog.` | `CompospressoLog.` |

Order matters: the longer FQN replacements run before the package-prefix replacement, so we don't accidentally rewrite `com.life360.android.doppio.doppioutils` to `dev.eddev.compospresso.doppioutils` before the first row matches.

- [ ] **Step 1: Create the copy + rewrite shell script**

Save as `~/Workspace/ed-devtech/compospresso/scripts/extract.sh` (the `scripts/` dir is for tooling only; we'll `.gitignore`-or-keep at our discretion; for now, keep committed for reproducibility).

```bash
#!/usr/bin/env bash
set -euo pipefail

SRC="/Users/eduard/Downloads/android/testsupport/src/main/java/com/life360/android/doppio"
DST="$(cd "$(dirname "$0")/.." && pwd)/library/src/main/java/dev/eddev/compospresso"

mkdir -p "$DST"

# Copy directory tree, skipping doppioutils/ (replaced by internal/) and README.md (handled separately).
cd "$SRC"
find . -type f -name '*.kt' ! -path './doppioutils/*' | while read -r f; do
    target="$DST/${f#./}"
    mkdir -p "$(dirname "$target")"
    cp "$f" "$target"
done

# Rewrite in place. Order matters: longer FQNs first.
find "$DST" -type f -name '*.kt' -print0 | xargs -0 sed -i '' \
    -e 's|com\.life360\.android\.doppio\.doppioutils\.DoppioError|dev.eddev.compospresso.internal.CompospressoError|g' \
    -e 's|com\.life360\.android\.testsupport\.TestError|dev.eddev.compospresso.internal.CompospressoError|g' \
    -e 's|com\.life360\.android\.testsupport\.TestLog|dev.eddev.compospresso.internal.CompospressoLog|g' \
    -e 's|com\.life360\.android\.testsupport\.TAG|dev.eddev.compospresso.internal.TAG|g' \
    -e 's|com\.life360\.android\.doppio|dev.eddev.compospresso|g' \
    -e 's|\bDoppioError(|CompospressoError(|g' \
    -e 's|\bTestError(|CompospressoError(|g' \
    -e 's|\bTestLog\.|CompospressoLog.|g'

# Rename the typo: AssertHelpter.kt -> AssertHelper.kt
if [ -f "$DST/assertion/AssertHelpter.kt" ]; then
    mv "$DST/assertion/AssertHelpter.kt" "$DST/assertion/AssertHelper.kt"
fi

echo "Extraction complete. Copied $(find "$DST" -name '*.kt' | wc -l | tr -d ' ') Kotlin files."
```

- [ ] **Step 2: Run the extraction script**

```bash
chmod +x scripts/extract.sh
./scripts/extract.sh
```

Expected: `Extraction complete. Copied 20 Kotlin files.` (The internal/ files from Task 3 are already there but not under the copy root — count is the new files only.)

Verify directory listing:

```bash
find library/src/main/java/dev/eddev/compospresso -type f -name '*.kt' | sort
```

Expected output (24 files total — 3 internal + 21 extracted):

```
library/src/main/java/dev/eddev/compospresso/assertion/AssertHelper.kt
library/src/main/java/dev/eddev/compospresso/assertion/TextAssertion.kt
library/src/main/java/dev/eddev/compospresso/assertion/VisibilityAssertion.kt
library/src/main/java/dev/eddev/compospresso/interaction/Clicks.kt
library/src/main/java/dev/eddev/compospresso/interaction/Date.kt
library/src/main/java/dev/eddev/compospresso/interaction/EditText.kt
library/src/main/java/dev/eddev/compospresso/interaction/PerformAction.kt
library/src/main/java/dev/eddev/compospresso/interaction/Swipe.kt
library/src/main/java/dev/eddev/compospresso/interaction/Wait.kt
library/src/main/java/dev/eddev/compospresso/interaction/Zoom.kt
library/src/main/java/dev/eddev/compospresso/internal/CompospressoError.kt
library/src/main/java/dev/eddev/compospresso/internal/CompospressoLog.kt
library/src/main/java/dev/eddev/compospresso/internal/Tag.kt
library/src/main/java/dev/eddev/compospresso/matcher/HelperMatchers.kt
library/src/main/java/dev/eddev/compospresso/matcher/ImageMatchers.kt
library/src/main/java/dev/eddev/compospresso/matcher/ResourceTypeMatcher.kt
library/src/main/java/dev/eddev/compospresso/matcher/TextMatchers.kt
library/src/main/java/dev/eddev/compospresso/uielement/ComposeElement.kt
library/src/main/java/dev/eddev/compospresso/uielement/MatcherViewElement.kt
library/src/main/java/dev/eddev/compospresso/uielement/UiElement.kt
library/src/main/java/dev/eddev/compospresso/uielement/UiElementLoggingDecorator.kt
library/src/main/java/dev/eddev/compospresso/uielement/UiSelectorElement.kt
library/src/main/java/dev/eddev/compospresso/uielement/compose/ComposeMatcher.kt
library/src/main/java/dev/eddev/compospresso/uielement/compose/ComposeTestRegistry.kt
library/src/main/java/dev/eddev/compospresso/uielement/matcher_view/MatcherView.kt
library/src/main/java/dev/eddev/compospresso/uielement/matcher_view/MatcherViewBuilder.kt
library/src/main/java/dev/eddev/compospresso/uielement/uiautomator/UiSelectorUtil.kt
```

If the file count or paths differ, stop and investigate before continuing.

- [ ] **Step 3: Verify no stale L360 references remain in the copied tree**

Run:

```bash
grep -rn "com\.life360\|DoppioError\|TestError\|TestLog\b" library/src/main/java/dev/eddev/compospresso/ || echo "clean"
```

Expected: `clean`. Any matches mean a rewrite rule was missed — fix the sed expression and re-run the extraction script (it overwrites cleanly because the script copies fresh each run).

- [ ] **Step 4: Verify the `AssertHelpter.kt` rename took effect inside the file**

The class/file is referenced only by filename — the typo was filename-only and there is no symbol named `AssertHelpter` in code (verified by grep on the L360 source). So no in-file rename is needed beyond the filename change the script performed. Confirm:

```bash
grep -rn "AssertHelpter" library/ || echo "no stale typo references"
```

Expected: `no stale typo references`.

- [ ] **Step 5: Commit the extracted sources (before the build attempt — captures the pre-fix state)**

```bash
git add scripts/ library/src/main/java/dev/eddev/compospresso/
git commit -m "Extract Doppio sources with package and import rewrites"
```

---

## Task 5: Build verification and fix-forward

**Goal:** Get `./gradlew :library:assembleDebug` green. Any compile errors are either (a) a missed rewrite, (b) a missing dependency in `library/build.gradle.kts`, or (c) a remaining L360-internal API call that needs a stub.

The Doppio sources have no unit tests, and we are doing a pure extract with no behavioral changes — so the build itself IS our correctness gate.

- [ ] **Step 1: Run the build**

```bash
./gradlew :library:assembleDebug
```

- [ ] **Step 2: If the build fails, triage by the first error class**

Open `build/reports/` or read the stderr. Match the failure to the categories below.

**(a) Missing import / unresolved reference to a `com.life360.*` symbol other than the three we replaced:**

Grep the L360 source for the symbol's definition:

```bash
grep -rn "fun <symbol>\|class <symbol>\|object <symbol>\|val <symbol>" /Users/eduard/Downloads/android/testsupport/src/main/java/com/life360/android/testsupport/
```

If the symbol is small and self-contained (utility extension, simple data class), inline it under `library/src/main/java/dev/eddev/compospresso/internal/` and add a rewrite to the extraction script. Re-run `scripts/extract.sh`, then re-run the build.

If the symbol pulls in a large transitive surface (Dagger, RxJava, Joda — none expected, but check), stop and surface the dependency to the user before proceeding.

**(b) Missing third-party dependency (e.g., `com.google.android.material.textfield.TextInputLayout` unresolved):**

Add the missing `api(libs.<name>)` line to `library/build.gradle.kts`, add the alias to `gradle/libs.versions.toml` if not already present, and re-run the build.

**(c) Compose API mismatch (compose-bom 2024.10.01 ships newer Compose UI than 2025.06.01 used by L360):**

Read the compile error, locate the call site in the extracted source, adjust to the current Compose API. Note the change in a comment block at the top of the file with the form `// Compose API adjustment: <symbol> renamed/moved in <version>`.

Re-run the build after each fix. Iterate until green.

- [ ] **Step 3: Verify build is green**

```bash
./gradlew :library:assembleDebug
```

Expected: `BUILD SUCCESSFUL`. AAR appears at `library/build/outputs/aar/library-debug.aar`.

- [ ] **Step 4: Verify release variant also builds**

```bash
./gradlew :library:assembleRelease
```

Expected: `BUILD SUCCESSFUL`. AAR at `library/build/outputs/aar/library-release.aar`.

- [ ] **Step 5: Commit any fix-forward changes**

If Step 2 required fixes, commit them now:

```bash
git add library/ scripts/
git commit -m "Fix-forward extracted sources for clean build"
```

If no fixes were needed, skip this commit.

---

## Task 6: README

**Files:**
- Create: `README.md` (at repo root)

The Doppio README is at `/Users/eduard/Downloads/android/testsupport/src/main/java/com/life360/android/doppio/README.md`. Adapt it:

- Replace title `# Doppio` → `# Compospresso`
- Replace tagline: "L360 in-house framework owned by Android SDET" → "An Android UI-testing wrapper over Espresso, UiAutomator, and Jetpack Compose."
- Delete the "Who is in charge?" section entirely
- Delete the sentence "It is designed to be used across all company projects" — replace with "It is designed to reduce boilerplate and handle common UI testing challenges (scrolling, ambiguous matchers, etc.)."
- Update all code blocks: nothing in the API examples needs to change (the user-facing types like `UiElement`, `MatcherView`, `ComposeMatcher` are unchanged), but if any code block references an L360 type, replace it.
- Add a short installation block at the top:

```markdown
## Installation

```groovy
dependencies {
    androidTestImplementation("dev.eddev:compospresso:0.1.0")
}
```

Currently published to `mavenLocal()` only. Run `./gradlew :library:publishToMavenLocal` after cloning.
```

- [ ] **Step 1: Read the source README**

```bash
cat /Users/eduard/Downloads/android/testsupport/src/main/java/com/life360/android/doppio/README.md
```

- [ ] **Step 2: Write the adapted README to `~/Workspace/ed-devtech/compospresso/README.md`**

Apply the edits listed above. No new sections, no new examples — just the renames and removals.

- [ ] **Step 3: Verify the README has no remaining L360 / Doppio references**

```bash
grep -niE "l360|life360|doppio" README.md || echo "clean"
```

Expected: `clean`.

- [ ] **Step 4: Commit**

```bash
git add README.md
git commit -m "Add Compospresso README adapted from Doppio"
```

---

## Task 7: Publish to mavenLocal and verify the artifact

- [ ] **Step 1: Publish**

```bash
./gradlew :library:publishToMavenLocal
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 2: Verify artifact landed in `~/.m2/repository/dev/eddev/compospresso/0.1.0/`**

```bash
ls ~/.m2/repository/dev/eddev/compospresso/0.1.0/
```

Expected output includes:

```
compospresso-0.1.0.aar
compospresso-0.1.0.module
compospresso-0.1.0.pom
compospresso-0.1.0-sources.jar
```

If any are missing, re-check the `publishing {}` block in `library/build.gradle.kts` (Task 2 Step 1) — most commonly missing is `withSourcesJar()` inside `singleVariant("release")`.

- [ ] **Step 3: Sanity-check the POM lists the expected api-scoped dependencies**

```bash
grep -E "<groupId>|<artifactId>|<scope>" ~/.m2/repository/dev/eddev/compospresso/0.1.0/compospresso-0.1.0.pom | head -40
```

Expected: deps for espresso-core, espresso-intents, uiautomator, material, appcompat, coordinatorlayout, compose-bom, ui-test-junit4 — all with `<scope>compile</scope>` (Maven's term for what Gradle calls `api`). `androidx-test-core` should be `<scope>runtime</scope>` (Gradle `implementation`).

- [ ] **Step 4: No commit needed** — publishing produces build artifacts, not source changes.

---

## Task 8: Final sanity sweep

- [ ] **Step 1: Confirm git history is clean**

```bash
git log --oneline
```

Expected (most recent first):

```
<hash> Add Compospresso README adapted from Doppio
<hash> Fix-forward extracted sources for clean build   # only if Task 5 Step 5 ran
<hash> Extract Doppio sources with package and import rewrites
<hash> Add internal replacements: CompospressoError, CompospressoLog, TAG
<hash> Configure :library module (AGP, deps, maven-publish)
<hash> Bootstrap Gradle project for Compospresso
<hash> Add Compospresso extraction design spec
```

- [ ] **Step 2: Confirm working tree is clean**

```bash
git status
```

Expected: `nothing to commit, working tree clean` (the only allowed untracked items are `build/` and `.gradle/`, which are `.gitignore`d).

- [ ] **Step 3: Confirm no stray L360 strings anywhere tracked**

```bash
git grep -niE "l360|life360|doppio"
```

Expected: matches ONLY in `docs/superpowers/specs/2026-06-08-compospresso-extraction-design.md` and `docs/superpowers/plans/2026-06-08-compospresso-extraction.md` (the design + this plan reference the source by name). No matches in `README.md`, `library/`, `scripts/`, or any build files.

- [ ] **Step 4: Confirm full build + publish cycle works from clean**

```bash
./gradlew clean :library:assembleDebug :library:assembleRelease :library:publishToMavenLocal
```

Expected: `BUILD SUCCESSFUL`.

---

## Done

The library is extracted, builds, publishes locally, and has no L360 dependencies. Any consumer can now add:

```kotlin
repositories { mavenLocal() }
dependencies { androidTestImplementation("dev.eddev:compospresso:0.1.0") }
```
