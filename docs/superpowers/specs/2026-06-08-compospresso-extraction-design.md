# Compospresso — Extraction Design

Source: `com.life360.android.doppio` package inside the `:testsupport` module of the L360 android monorepo (`~/Downloads/android`).

Target: new standalone Android library repo at `~/Workspace/ed-devtech/compospresso/`.

## 1. Goal

Lift the in-house Doppio UI-testing wrapper out of the L360 monorepo and republish it as an independent, brand-neutral Android library named **Compospresso**, owned by `ed-devtech`.

Scope is a one-time **fresh extract**:

- No back-sync to the L360 monorepo.
- No behavioral changes to the wrapper code.
- No sample/demo app, no CI, no Maven Central publishing.

## 2. Repo layout

```
~/Workspace/ed-devtech/compospresso/
├── settings.gradle.kts             # rootProject.name = "compospresso", includes :library
├── build.gradle.kts                # top-level: AGP + Kotlin plugin declarations
├── gradle/libs.versions.toml       # version catalog
├── gradle.properties
├── gradlew, gradlew.bat, gradle/wrapper/
├── README.md                       # adapted from Doppio README
├── LICENSE                         # Apache-2.0
└── library/
    ├── build.gradle.kts            # com.android.library, namespace dev.eddev.compospresso
    └── src/main/java/dev/eddev/compospresso/
        ├── uielement/
        │   ├── compose/
        │   ├── matcher_view/
        │   └── uiautomator/
        ├── assertion/
        ├── matcher/
        ├── interaction/
        └── internal/               # NEW — replacements for L360 testsupport deps
```

Single `:library` module. No sample app.

## 3. Package and symbol renames

| Old (L360) | New (Compospresso) |
|---|---|
| `com.life360.android.doppio.*` | `dev.eddev.compospresso.*` |
| `DoppioError` | `CompospressoError` |
| `com.life360.android.testsupport.TestError` | folded into `CompospressoError` |
| `com.life360.android.testsupport.TestLog` | `dev.eddev.compospresso.internal.CompospressoLog` |
| `com.life360.android.testsupport.TAG` | private const in `dev.eddev.compospresso.internal.Tag` |

`TestError` and `DoppioError` collapse into a single error type. `TestLog` becomes a thin wrapper over `android.util.Log` — no Amplitude/metrics, no analytics surface.

No aliases for the old `Doppio*` names. Clean break.

## 4. Files extracted (verbatim except renames)

From `~/Downloads/android/testsupport/src/main/java/com/life360/android/doppio/`:

- `uielement/` — `UiElement.kt`, `ComposeElement.kt`, `MatcherViewElement.kt`, `UiSelectorElement.kt`, `UiElementLoggingDecorator.kt`
- `uielement/compose/` — `ComposeMatcher.kt`, `ComposeTestRegistry.kt`
- `uielement/matcher_view/` — `MatcherView.kt`, `MatcherViewBuilder.kt`
- `uielement/uiautomator/` — `UiSelectorUtil.kt`
- `assertion/` — `AssertHelpter.kt` (rename → `AssertHelper.kt`), `TextAssertion.kt`, `VisibilityAssertion.kt`
- `matcher/` — `HelperMatchers.kt`, `ImageMatchers.kt`, `ResourceTypeMatcher.kt`, `TextMatchers.kt`
- `interaction/` — `Clicks.kt`, `Date.kt`, `EditText.kt`, `PerformAction.kt`, `Swipe.kt`, `Wait.kt`, `Zoom.kt`
- `doppioutils/DoppioError.kt` → `internal/CompospressoError.kt` (merged with TestError)

New files created:

- `internal/CompospressoLog.kt`
- `internal/Tag.kt`

## 5. Gradle / build

- **AGP**: 8.x (latest stable)
- **Kotlin**: 2.x
- **JDK toolchain**: 17
- **compileSdk**: 34
- **minSdk**: 24
- **namespace**: `dev.eddev.compospresso`

Dependency scoping — types exposed in the public API use `api`, internal-only deps use `implementation`:

- `api`
  - `androidx.test.espresso:espresso-core`
  - `androidx.test.espresso:espresso-intents`
  - `androidx.test.uiautomator:uiautomator`
  - `androidx.compose.ui:ui-test-junit4`
  - `com.google.android.material:material` (for `TextInputLayout`)
  - `androidx.appcompat:appcompat`
  - `androidx.coordinatorlayout:coordinatorlayout`
- `implementation`
  - `androidx.test:core`
- Hamcrest is pulled transitively via Espresso.

Publishing: `maven-publish` plugin configured to publish the AAR + sources jar to `mavenLocal()`. No remote repository wired up.

## 6. README

Doppio's README is the base. Replace the name, drop L360-specific lines (the "Who is in charge" section, references to "L360 in-house framework", "company projects"), and update every code block to the new package. Keep the API usage, examples, and Compose-support caveats.

## 7. Git

- `git init -b main` in `~/Workspace/ed-devtech/compospresso/`
- One initial commit: `Initial extraction from Doppio`
- No remote configured.

## 8. Out of scope

- Back-sync to the L360 monorepo.
- Replacing the L360 copy of Doppio with a dep on Compospresso.
- CI/CD, Maven Central publishing, signing, release automation.
- Sample/demo app, instrumentation smoke tests.
- Behavioral changes, refactors, or API cleanup beyond the rename + internal-dep inlining.
