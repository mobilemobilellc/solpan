# Copilot Instructions for SolPan

SolPan is a single-module Android app (`:app`) that helps users orient solar panels using sun position calculations and phone sensors. Package: `app.mobilemobile.solpan`.

## Build & Lint Commands

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Run all unit tests
./gradlew :app:testDebugUnitTest

# Run a single unit test class
./gradlew :app:testDebugUnitTest --tests "app.mobilemobile.solpan.ExampleUnitTest"

# Lint: Spotless (ktlint + ktfmt) and Detekt
./gradlew app:spotlessCheck app:detekt

# Auto-fix formatting issues
./gradlew app:spotlessApply

# Full CI check (matches CI pipeline)
./gradlew app:detekt app:spotlessCheck :app:testDebugUnitTest :app:assembleDebug --parallel --continue
```

## Architecture

**Single Activity, Compose-only UI.** `SolPanActivity` sets up the theme and hosts `SolPanApp()`, which uses **Navigation3** (`NavigationSuiteScaffold`) with serializable `NavKey` data classes for type-safe routing. Bottom navigation has 5 tabs — one per `TiltMode` — each showing `SolPanScreen` with different parameters. An `AboutLibrariesScreen` is also reachable.

**State management** uses `SolPanViewModel` + `StateFlow`. Screens collect state via `collectAsState()`. The ViewModel combines location, magnetic declination, and tilt mode flows to produce `OptimalPanelParameters`. Location updates are debounced (300ms) and flows use `WhileSubscribed(5000)`.

**No DI framework.** Dependencies are wired through Compose's `remember {}`, composable-scoped controllers (`rememberDeviceLocationController()`, `rememberDeviceOrientationController()`), and a manual `ViewModelProvider.Factory`.

**Key domain packages:**
- `solar/` — `SolarCalculator` object wraps the **commons-suncalc** library for sun position; also computes shortest azimuth difference (normalized to ±180°)
- `orientation/` — `DeviceOrientationController` fuses accelerometer + magnetometer data through a low-pass filter (α=0.08), producing `OrientationData`
- `location/` — `DeviceLocationManager` wraps Google Play Services Fused Location Provider (10s interval, HIGH_ACCURACY)
- `data/` — Pure data classes (`TiltMode`, `SolarPosition`, `OptimalPanelParameters`, `LocationData`, `OrientationData`)

**Tilt modes** calculate optimal panel angles differently:
- REALTIME: tracks current sun position
- SUMMER/WINTER: latitude ± 23.5° (Earth's axial tilt)
- SPRING_AUTUMN/YEAR_ROUND: equal to latitude
- All non-realtime modes use 180° (South) for northern hemisphere, 0° (North) for southern

## Conventions

- **License headers** are required on all Kotlin and Gradle files. The template is at `configs/spotless/copyright.kt` and is enforced by Spotless.
- **ktlint** uses `ktlint_official` code style with experimental rules enabled. Star imports are disabled (`name_count_to_use_star_import = 2147483647`). Compose-specific lint rules are applied via `io.nlopez.compose.rules`. Config: `configs/spotless/.editorconfig`.
- **Detekt** config is at `configs/detekt/detekt.yml` with a baseline at `configs/detekt/detekt-baseline.xml`. `maxIssues: 0` — all issues must be resolved or baselined.
- **Kotlin serialization** is used for `NavKey` classes and `TiltMode` enum (for Navigation3 state persistence).
- **JDK 21** toolchain is required (`kotlin { jvmToolchain(21) }`), though CI uses JDK 17 setup.
- **`@Composable` functions** are exempt from ktlint function naming rules (PascalCase allowed).
- UI components use **Material 3 ExpressiveTheme** with custom light/dark color schemes.
- Reusable card patterns: `InfoCard` (header with icon + title + content) and `InfoRow` (label-value pair) in `ui/screen/components/InfoComposables.kt`.
- Firebase (Analytics, Crashlytics, Performance) is integrated — `google-services.json` is not committed (decoded from a CI secret).
- The app supports **29 locales** configured via `generateLocaleConfig` with string resources.
