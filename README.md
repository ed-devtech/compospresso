# Compospresso

An Android UI-testing wrapper over Espresso, UiAutomator, and Jetpack Compose. It reduces boilerplate and handles common UI testing challenges (scrolling, ambiguous matchers, etc.).

## Installation

```groovy
dependencies {
    androidTestImplementation("dev.eddev:compospresso:0.1.0")
}
```

Currently published to `mavenLocal()` only. Run `./gradlew :library:publishToMavenLocal` from a local clone before using.

# How to

## UI Elements Wrapper: `UiElement`

`UiElement` is a unified interface for interacting with UI elements in Android tests. It wraps Espresso, UiAutomator, and Jetpack Compose elements, providing a consistent API for clicks, text input, assertions, and more.

**Rule of thumb:** When interacting with the app under test (not OS or other apps), use Espresso-based elements. Use UiAutomator for system dialogs or cross-app UI. Compose support is available for Compose-based screens.

---

## UiElement Constructors

### Espresso (View-based)

```
UiElement(resId: Int)
UiElement(vararg resIds: Int)
UiElement(resId: Int, text: String)
UiElement(matcher: Matcher<View>)
UiElement(matcherView: MatcherView)
UiElement(builder: MatcherViewBuilder)
```

- **By resource ID:**
  ```
  val button = UiElement(R.id.button)
  val buttonWithText = UiElement(R.id.button, "Submit")
  ```
- **By matcher:**
  ```
  val custom = UiElement(withText("Hello"))
  ```
- **By builder:**
  ```
  val complexEle = UiElement(MatcherView.builder().with(R.id.parent).withChild(R.id.child))
  val complexEle = UiElement(MatcherView.builder(R.string.targetText).withAncestor(R.id.ancestor))
  val complexEle = MatcherView.builder().with(R.id.parent).withChild(R.id.child).buildElement()
  val complexEle = MatcherView.builder(R.id.targetID).withAncestor(R.id.ancestorId).buildElement()
  val complexEle = MatcherViewBuilder(R.id.edit_text).withAncestor(R.id.last_name_edit_text).buildElement()
  ```

### UiAutomator (System/UI outside app)

```
UiElement(selector: UiSelector)
```

- Example:
  ```
  val sysDialog = UiElement(UiSelector().text("Allow"))
  ```

### Jetpack Compose

```
UiElement(composeMatcher: ComposeMatcher)
```

- Example:
  ```
  val composeElem = UiElement(ComposeMatcher.withTestTag("myTag"))
  ```
  *Note: Compose support is limited; see below for details.*

### Logging Decorator

Add logging to any UiElement:
```
val loggedElem = UiElement(R.id.button).withLogging("Button")
```

---

## UiElement API (Methods)

All UiElement variants implement the following methods (some may throw if not supported by the backend):

- `tap(timeOut: Long = 0L)` — Tap/click the element (waits up to `timeOut` ms for visibility)
- `longTap()` — Long press
- `doubleTap()` — Double tap (Espresso only)
- `putText(text: String, timeOut: Long = 0L)` — Type text into input
- `clearTextInput()` — Clear text field (Espresso only)
- `setDate(year: Int, month: Int, day: Int)` — Set date (Espresso only)
- `swipe(direction: Direction)` — Swipe in a direction (Espresso only)
- `pinchIn(params: Map<String, Int>? = null)` — Pinch in/zoom out (Espresso/UiAutomator)
- `pinchOut(params: Map<String, Int>? = null)` — Pinch out/zoom in (Espresso/UiAutomator)
- `perform(action: ViewAction)` — Perform custom Espresso action (Espresso only)
- `isDisplayed(): Boolean` — Is element visible?
- `isEnabled(): Boolean` — Is element enabled?
- `isChecked(): Boolean` — Is checkbox/switch checked?
- `getText(): String` — Get text content
- `waitDisplayed(timeOut: Long): Boolean` — Wait until displayed
- `waitNotDisplayed(timeOut: Long): Boolean` — Wait until not displayed
- `waitContains(expectedSubstring: String, timeout: Long, contains: Boolean, pullingInterval: Long = 250L): Boolean` — Wait until text contains/does not contain substring
- `getPoint(): Point` — Get screen coordinates

*Some methods may not be implemented for all backends (e.g., Compose, UiAutomator). Unsupported calls will throw a `TestError` or `NotImplementedError`.*

---

## Usage Patterns & Best Practices

- **Prefer Espresso for in-app UI:**
  ```
  val button = UiElement(R.id.button)
  button.tap()
  button.putText("Hello")
  assertTrue(button.isDisplayed())
  ```
- **Use UiAutomator for system dialogs or cross-app UI:**
  ```
  val allowButton = UiElement(UiSelector().text("Allow"))
  allowButton.tap()
  ```
- **Use Compose for Compose-based screens:**
  ```
  val composeElem = UiElement(ComposeMatcher.withTestTag("tag"))
  composeElem.tap()
  // Many methods are not yet implemented for Compose
  ```
- **Add logging for better test diagnostics:**
  ```
  val logged = UiElement(R.id.button).withLogging("Login Button")
  logged.tap()
  ```
- **Advanced: Use builder for complex matchers:**
  ```
  val elem = UiElement(MatcherView.builder().with(R.id.parent).withChild(R.id.child).buildMatcher())
  ```

---

## Example: Page Object Pattern

```
private object LoginScreenElements {
    val username = UiElement(R.id.username)
    val password = UiElement(R.id.password)
    val loginButton = UiElement(R.id.login)
}

class LoginScreen {
    fun login(user: String, pass: String) {
        LoginScreenElements.username.putText(user)
        LoginScreenElements.password.putText(pass)
        LoginScreenElements.loginButton.tap()
    }
}
```

---

## Compose Support

- Compose support is experimental and limited. Only `tap`, `isDisplayed`, and `waitDisplayed` are implemented. Other methods will throw `NotImplementedError`.
- Use `UiElement(ComposeMatcher.withTestTag("tag"))` for Compose nodes.

---

## Assertions API

Visibility and text assertions are provided for all UiElement types:

```
assertDisplayed(matcher: Matcher<View>)
assertDisplayed(viewId: Int)
assertDisplayed(text: String)
assertDisplayed(@IdRes viewId: Int, text: String)
assertDisplayed(@IdRes viewId: Int, @StringRes stringId: Int)
assertDisplayed(uiElement: UiElement)

assertHasText(uiElement: UiElement, text: String)
```

---

## Test Rules

Reset app data between tests using provided rules:

```
// Clear all app's SharedPreferences
@get:Rule
val clearPreferencesRule = ClearPreferencesRule()

// Delete all tables from all the app's SQLite Databases
@get:Rule
val clearDatabaseRule = ClearDatabaseRule()

// Delete all files in getFilesDir() and getCacheDir()
@get:Rule
val clearFilesRule = ClearFilesRule()
```

Or use [Android Test Orchestrator](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner#use-android) to sandbox tests.

---

# Why not just use Espresso and UiAutomator directly?

Espresso can be very wordy and repetitive. Compospresso's `UiElement` provides a concise, unified API and adds missing features (waits, logging, advanced assertions, Compose support, etc).

**Example:**
```
// Espresso:
onView(anyOf(withText(string), allOf(withParent(isAssignableFrom(TextInputLayout::class.java)), hasDescendant(withText(string)))).check(matches(isDisplayed()))
// Compospresso:
assertDisplayed(UiElement(string))
```

---

# Contributing

- Prefer Kotlin for new code.
- Avoid direct use of `android.widget.*` types unless necessary.
- Add new utilities and helpers as needed to make UI tests easier and more robust.
