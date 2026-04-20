<!--
  ~ Copyright 2025 MobileMobile LLC
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
-->

# KDoc and API Documentation Guide for SolPan

**Last Updated:** April 2026
**Format:** Kotlin Documentation (KDoc)
**Generation:** Dokka HTML documentation

This guide establishes documentation standards for all public APIs in SolPan, ensuring clarity, consistency, and discoverability through IDE assistance and generated documentation sites.

## Overview

SolPan uses **KDoc** to document all public Kotlin APIs:
- **Classes**: Architecture, responsibilities, threading, lifecycle
- **Functions**: Parameters, return values, exceptions, examples
- **Properties**: Purpose, valid ranges, nullability semantics

Generated **Dokka documentation** produces an HTML site for technical reference, ideal for:
- Team onboarding
- Client technical deep-dives
- IDE navigation and autocomplete
- GitHub Pages hosting

## KDoc Standards

### 1. Class/Interface Documentation

Every public class and interface must have a class-level KDoc explaining its purpose.

**Template:**
```kotlin
/**
 * Brief one-line description of the class.
 *
 * Longer description (1-2 paragraphs) explaining:
 * - What the class does
 * - When/how to use it
 * - Key responsibilities
 * - Thread safety (if relevant)
 * - Lifecycle (if relevant)
 *
 * ## Important Concepts
 *
 * Key architectural patterns or complex logic explained here.
 * Use subsections (##) to organize complex classes.
 *
 * @param constructor_param_1 Description of first constructor parameter
 * @param constructor_param_2 Description of second constructor parameter
 * @see RelatedClass if there are related classes to know about
 */
public class MyClass(
    val constructor_param_1: String,
    val constructor_param_2: Int,
) {
    // ...
}
```

**Example from SolPan:**
```kotlin
/**
 * Main state management ViewModel for SolPan.
 *
 * Combines multiple reactive streams (location, orientation, magnetic declination, preferences)
 * to produce a unified [uiState] that the UI observes. All state updates are reactive and
 * observable via StateFlow, ensuring the UI always reflects the current system state.
 *
 * ## Reactive Architecture
 *
 * The ViewModel follows a reactive architecture pattern:
 * - **Location updates** are debounced (300ms) to avoid excessive recomputation
 * - **Magnetic declination** is computed lazily from location changes
 * - **Solar calculations** are recomputed when tilt mode or location changes
 * - **Realtime mode** ticks every 30 seconds to update sun position
 * - **UI state** combines all streams with [WhileSubscribed(5000)] to auto-cleanup when UI is backgrounded
 *
 * @param initialMode The starting tilt mode (typically [TiltMode.REALTIME])
 * @param preferencesRepository User preferences (tutorial state, saved settings)
 * @param locationRepository Location stream and persistence
 * @param analytics Analytics event tracking
 * @param magneticDeclinationProvider Magnetic declination calculator (injectable for testing)
 */
class SolPanViewModel(
    val initialMode: TiltMode,
    private val preferencesRepository: UserPreferencesRepository,
    private val locationRepository: LocationRepository,
    private val analytics: AnalyticsTracker,
    private val magneticDeclinationProvider: MagneticDeclinationProvider = AndroidMagneticDeclinationProvider(),
) : ViewModel()
```

### 2. Function Documentation

Every public function must have KDoc explaining its purpose, parameters, and behavior.

**Template:**
```kotlin
/**
 * Brief description of what the function does.
 *
 * Longer description if needed, explaining:
 * - When to call this function
 * - What side effects it has
 * - Exception conditions
 * - Threading requirements
 *
 * @param paramName Description of this parameter. Include valid ranges or accepted values.
 * @param paramName2 Second parameter description
 * @return Description of the return value. Include null semantics if applicable.
 * @throws SomeException Conditions when this exception is thrown
 * @see RelatedFunction for related functions
 *
 * Example usage:
 * ```kotlin
 * val result = myFunction(param1, param2)
 * println(result)  // Prints...
 * ```
 */
public fun myFunction(paramName: String, paramName2: Int): String
```

**Example from SolPan:**
```kotlin
/**
 * Updates the current device orientation from sensors.
 *
 * Called by [DeviceOrientationController] when accelerometer/magnetometer data is ready.
 * Updates trigger [uiState] recomposition if the orientation data changed.
 *
 * @param orientation The latest device orientation (pitch, roll, azimuth)
 */
fun updateOrientation(orientation: OrientationData) {
    _currentOrientation.value = orientation
}
```

### 3. Property Documentation

Public properties should have brief KDoc, especially if they represent observable state.

**Template:**
```kotlin
/** Brief description of the property. Mention units if applicable, valid ranges, nullability. */
public val myProperty: StateFlow<SomeType>

/**
 * More detailed description if the property is complex.
 *
 * Use cases:
 * - Use case 1
 * - Use case 2
 */
public var mutableProperty: String
```

**Example from SolPan:**
```kotlin
/**
 * Magnetic declination (angle between true north and magnetic north) at current location.
 *
 * Used to convert between true azimuth (calculated from sun position) and magnetic azimuth
 * (what compass reads). Computed lazily as location changes. Null until location is available.
 *
 * Range: -180° to +180° (negative = magnetic north is west of true north)
 */
val magneticDeclinationFlow: StateFlow<Float?>
```

### 4. Complex Concepts: Use Sections

For complex classes or functions, use KDoc sections to organize explanations.

**Example:**
```kotlin
/**
 * Solar position calculator using ephemeris algorithms.
 *
 * Computes the sun's position in the sky (azimuth, altitude) at a given date/time/location.
 * Based on the commons-suncalc library, wrapping advanced astronomical calculations.
 *
 * ## Accuracy
 *
 * Typical accuracy is ±0.5° for azimuth and altitude under normal conditions.
 * Accuracy degrades at extreme latitudes (>80°) or high altitudes (>10km).
 *
 * ## Performance
 *
 * Single calculation takes ~1-2ms on modern devices. Suitable for real-time updates.
 * Consider caching results if calculating more than once per second.
 *
 * ## Thread Safety
 *
 * All methods are thread-safe and stateless. Safe to call from any thread.
 *
 * ## References
 *
 * - [Celestial Mechanics Algorithms](https://en.wikipedia.org/wiki/Position_of_the_Sun)
 * - [commons-suncalc Library](https://github.com/shred/commons-suncalc)
 */
object SolarCalculator
```

### 5. Linking and Cross-References

Use KDoc links to cross-reference related code, improving IDE navigation.

**Syntax:**
```kotlin
/**
 * This function works with [RelatedClass].
 * Compare to [OtherFunction] for similar functionality.
 * See [MyClass.myProperty] for related state.
 */
```

**Example:**
```kotlin
/**
 * Called by [DeviceOrientationController] when accelerometer/magnetometer data is ready.
 * Updates trigger [uiState] recomposition if the orientation data changed.
 */
fun updateOrientation(orientation: OrientationData)
```

### 6. Nullability and Default Values

Document nullability and defaults explicitly.

**Template:**
```kotlin
/**
 * @param optionalParam Description. Null if [SomeCondition], defaults to [defaultValue]
 * @return The computed result, or null if [SomeCondition] prevents computation
 */
public fun myFunction(optionalParam: String?): Result?
```

**Example:**
```kotlin
/**
 * Current user location from GPS or fused location provider.
 *
 * Null if permission not granted or location updates not yet available.
 */
val currentLocation: StateFlow<LocationData?>
```

### 7. Threading and Coroutines

Document threading requirements and suspension behavior.

**Template:**
```kotlin
/**
 * Suspends until the operation completes.
 *
 * Must be called from a coroutine context (e.g., viewModelScope.launch).
 *
 * @throws CancellationException if the coroutine is cancelled while waiting
 */
public suspend fun suspendingOperation()
```

### 8. Exceptions

Document exceptions with conditions that trigger them.

**Template:**
```kotlin
/**
 * Parses the input string as an integer.
 *
 * @param input The string to parse
 * @return The parsed integer
 * @throws NumberFormatException if input is not a valid integer (e.g., "abc")
 * @throws IllegalArgumentException if input is empty
 */
public fun parseInt(input: String): Int
```

### 9. Deprecated APIs

Mark deprecated APIs with @Deprecated and explain migration path.

**Template:**
```kotlin
/**
 * @deprecated Use [NewFunction] instead, which is faster and more flexible.
 * Migration: Replace `oldFunction(x)` with `newFunction(x, mode = Default)`
 */
@Deprecated("Use NewFunction instead")
public fun oldFunction(x: Int): Result
```

### 10. Code Examples in KDoc

Include concise code examples in KDoc using triple-backtick Markdown.

**Template:**
```kotlin
/**
 * Converts a temperature in Celsius to Fahrenheit.
 *
 * Example:
 * ```kotlin
 * val fahrenheit = celsiusToFahrenheit(25.0)
 * println(fahrenheit)  // Prints 77.0
 * ```
 *
 * @param celsius Temperature in Celsius
 * @return Temperature in Fahrenheit
 */
```

## Dokka Configuration

SolPan uses **Dokka** to generate HTML documentation from KDoc.

### Setup

In `app/build.gradle.kts`:

```kotlin
plugins {
    // ... other plugins
    id("org.jetbrains.dokka") version "1.9.20"
}

dokka {
    dokkaSourceSets {
        named("main") {
            displayName.set("SolPan API")
            includes.from(file("$projectDir/docs/index.md"))
            
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(uri("https://github.com/mobilemobilellc/solpan/blob/main/app/src/main/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}
```

### Generating Documentation

```bash
# Generate HTML documentation
./gradlew dokkaHtml

# Output: app/build/dokka/html/
```

### Hosting on GitHub Pages

Create `.github/workflows/docs.yml`:

```yaml
name: Generate and Deploy Documentation

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  docs:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v5
      
      - uses: actions/setup-java@v5
        with:
          distribution: temurin
          java-version: 21
      
      - name: Generate Dokka documentation
        run: ./gradlew dokkaHtml
      
      - name: Deploy to GitHub Pages
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./app/build/dokka/html/
```

## Best Practices

### ✅ Do

- **Document public APIs only** - Internal implementation details don't need KDoc
- **Use present tense** - "Calculates the result" not "Will calculate"
- **Be specific** - "Returns the user's location from GPS" is better than "Gets data"
- **Link related code** - Use [ClassName] and [functionName] for IDE navigation
- **Include edge cases** - Document null, empty, or error conditions
- **Add examples** - Real code examples help understanding
- **Update with code** - Keep KDoc synchronized with actual implementation

### ❌ Don't

- **Document trivial functions** - `getName() -> String` is self-explanatory
- **Repeat the signature** - "This function takes a String parameter" is redundant
- **Use abbreviations** - Spell out parameter names in descriptions
- **Overcomplicate** - 2-3 sentences is usually sufficient
- **Reference private implementation** - Only mention public APIs in docs
- **Forget about null** - Always clarify if return can be null

## Checklist: Documentation Review

Before committing code:

- [ ] All public classes have class-level KDoc
- [ ] All public functions have function-level KDoc
- [ ] Public properties have brief KDoc (at least 1 line)
- [ ] Parameters and returns are documented with @param/@return
- [ ] Exceptions are documented with @throws
- [ ] Complex logic includes ## sections for clarity
- [ ] KDoc links ([ClassName]) are used for cross-references
- [ ] Code examples are present for complex APIs
- [ ] Deprecated APIs have @Deprecated with migration path
- [ ] No KDoc contains markdown that won't render in HTML

## IDE Integration

### Android Studio / IntelliJ IDEA

KDoc is automatically integrated:
- Hover over a symbol to see KDoc in quick documentation
- Cmd/Ctrl+J to view documentation
- KDoc shows in autocomplete suggestions
- IDE highlights code examples in KDoc

### Gradle

Generate documentation locally:

```bash
./gradlew dokkaHtml
open app/build/dokka/html/index.html
```

Watch for changes:

```bash
./gradlew -t dokkaHtml
```

## SolPan Documentation Coverage

Current state:

- ✅ SolPanViewModel (comprehensive, 50+ lines of KDoc)
- ✅ SolPanUiState (data class, documented)
- ✅ TiltMode (enum, documented)
- 🔄 UI Components (being enhanced)
- 🔄 Repository interfaces (being enhanced)
- 🔄 Solar calculations (being enhanced)

## References

- [Kotlin KDoc Documentation](https://kotlinlang.org/docs/kotlin-doc.html)
- [Dokka User Guide](https://kotlin.github.io/dokka/1.9.20/)
- [Android API Documentation Style](https://developer.android.com/guide/topics/documentation)
- [Google's Java Documentation Guide](https://google.github.io/styleguide/javaguide.html#s7-javadoc) (apply principles to Kotlin)

---

**Last Verified:** April 20, 2026
**Dokka Version:** 1.9.20
**Kotlin Version:** 2.3.0
