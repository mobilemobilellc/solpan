# Roadmap

What is unfinished, and what is planned. Everything here has been checked against the build or the running app rather than assumed.

## Unfinished

### UI code has no tests

Tests now live in the module they cover: `core:solar` holds the solar and alignment tests, `core:data` the preferences tests, `feature:optimizer` the ViewModel and tutorial tests with their fakes, and `:app` the formatting and orientation-reading tests. Library modules emit coverage data, and the aggregate report spans all of them.

That leaves the real gap, which was always the UI:

| Package | Lines | Coverage |
|---|---|---|
| `solpan.model` | 80 | 100% |
| `solpan.optimizer` | 150 | 71% |
| `solpan.data` | 22 | 86% |
| `solpan.ui.screen.components` | 406 | 0% |
| `solpan.ui.components` | 380 | 0% |
| `solpan.ui.screen` | 153 | 0% |
| `solpan.orientation` | 80 | 18% |

Overall 15.02%, 235 of 1565 lines. Roughly 940 of the uncovered lines are composables, where screenshot tests are a better fit than unit tests. The rest of `solpan.orientation` is the sensor listener itself, which needs a `SensorManager` fake to reach.

### The coverage floor

`app:jacocoCoverageVerification` runs in CI and fails the build below **12%** line coverage. Current is **15.02%**. Extracting uncovered helpers lowers the figure without removing a test, so raise the floor when coverage rises rather than leaving the headroom for the next extraction to spend.

The gate was checked both ways, passing at 12% and failing at 50%, so it does bite. `MINIMUM_LINE_COVERAGE` lives in `JacocoReportConventionPlugin`.

### detekt now covers every module

All 37 findings were fixed in code rather than baselined, and `configs/detekt/detekt-baseline.xml` is deleted. What changed:

- 25 magic numbers became named constants, which is where most of the value was: `QUARTER_TURN_DEGREES` and `AZIMUTH_TO_SCREEN_ANGLE` say what `90.0` meant in compass geometry, `LOCATION_UPDATE_INTERVAL_MS` what `10000L` meant.
- `GuidanceCard` dropped from complexity 25 to under 15 by extracting the azimuth, tilt and roll guidance into three composable helpers.
- `AzimuthAwareBubbleLevel` dropped from 21 to under 15, and from 166 lines to 127, by moving its accessibility string and three canvas phases into functions.
- `DeviceLocationManager` catches `IllegalStateException` rather than `Exception`, and `LinkInfo` moved to its own file.

Two rules were turned off rather than obeyed, both with a reason in `detekt.yml`:

- The whole `formatting` ruleset, 108 ktlint rules bundled from detekt's much older ktlint. Spotless already owns formatting, and the two disagree about ktfmt's continuation indents.
- `LongMethod` for `@Composable` functions. Five composables are still over 60 lines, at 72 to 127. `CyclomaticComplexMethod` stays on for composables and is what caught the two genuinely tangled ones, so the meaningful signal is still enforced. Verified: a deliberately complex composable still fails the build.

### The Baseline Profile does not generate yet

`androidx.baselineprofile` is applied to `:app` and `:baselineprofile`, the benchmark sources compile, and `:app:generateBaselineProfile` builds, installs and runs all 7 benchmarks on a device. What it does not do yet is produce a profile, so `app/src/main/baseline-prof.txt` is still three comment lines and startup gets no benefit.

Measured on a Pixel 6a running Android 17:

- `BaselineProfileGenerator.generate` throws `Unable to confirm activity launch completion` from `MacrobenchmarkScope.amStartAndWait`.
- The six `MacrobenchmarkRule` tests are skipped by `AssumptionViolatedException` from the rule's own device-state checks.

Both are device-state gates rather than code faults. Clearing them means either a dedicated benchmark device, or setting `androidx.benchmark.suppressErrors` for the states that apply, which trades measurement accuracy for a result. Neither should be decided by whoever is nearest a phone.

### Preferences are not encrypted, and backup is on

`UserPreferencesRepository` uses plain `preferencesDataStore`. Nothing encrypts it. The manifest sets `android:allowBackup="true"` with `dataExtractionRules` and `fullBackupContent` rules.

Both rule files are still the unmodified Android Studio templates, so nothing is scoped and the platform default applies.

The only persisted value is a `tutorialSeen` boolean, so neither is exposing anything today. The decision should still be explicit: either encrypt preferences and scope backup, or keep both as they are and say so. See [SECURITY.md](SECURITY.md).

### Smaller items

- Gradle reports `Deprecated Gradle features were used in this build, making it incompatible with Gradle 10`. Run with `--warning-mode all` to find them.
- 18 open Dependabot alerts, 1 critical and 7 high, in netty, bouncycastle, jackson and wire. These read as build classpath surfaced by dependency submission rather than anything shipped in the APK, but which configuration they sit on has not been verified.

## Planned

Ideas that are wanted but not started. None of these are committed to.

### Accessibility

TalkBack labels and content descriptions across the alignment UI, a large-text pass, and contrast checks against the outdoor use case the app is for. The alignment visualiser is the hard part: it conveys state through position and colour, both of which need a non-visual equivalent.

### Per-module quality gates

`spotlessCheck` and `testDebugUnitTest` already run unscoped. `detekt` is the one still pinned to `:app`, pending the baseline decision above, and coverage is still enforced in aggregate rather than per module.

### Architecture decision records

A short `docs/adr/` set covering the choices already made: Navigation 3 over Navigation 2, DataStore over SharedPreferences, the `AnalyticsTracker` seam, and the module split itself.

### Release smoke check

The release train works end to end: release-please opens the PR, merging it tags, and `release.yml` builds, signs, attests provenance and publishes a completed release to the Play track implied by the tag. What is missing is any check between tagging and publishing, so a broken build reaches users once Play's review passes, before anyone has run it.

### Wider device coverage

The app targets phones and tablets. Foldables, and the large-screen posture changes that come with them, are untested.
