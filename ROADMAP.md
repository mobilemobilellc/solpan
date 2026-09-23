# Roadmap

What is unfinished, and what is planned. Everything here has been checked against the build or the running app rather than assumed.

## Unfinished

### UI code has no tests

Tests now live in the module they cover: `core:solar` holds the solar and alignment tests, `feature:optimizer` the ViewModel and tutorial tests with their fakes, and `:app` keeps only the formatting test. Library modules emit coverage data, and the aggregate report spans all of them.

That leaves the real gap, which was always the UI:

| Package | Lines | Coverage |
|---|---|---|
| `solpan.model` | 78 | 100% |
| `solpan.optimizer` | 137 | 68% |
| `solpan.ui.screen.components` | 384 | 0% |
| `solpan.ui.components` | 364 | 0% |
| `solpan.ui.screen` | 158 | 0% |
| `solpan.orientation` | 66 | 0% |

Overall 12.9%, 192 of 1493 lines. Roughly 900 of the uncovered lines are composables, where screenshot tests are a better fit than unit tests. `solpan.orientation` holds the sensor fusion and is the clearest unit-testable gap left.

### The coverage floor is low

`app:jacocoCoverageVerification` runs in CI and fails the build if line coverage drops below **12%**, against a current 12.86%. That is a ratchet against regression, not a target: it stops a change quietly removing tests or landing a large untested surface, and nothing more.

Raise `MINIMUM_LINE_COVERAGE` in `JacocoReportConventionPlugin` as coverage rises. The gate was checked both ways, passing at 12% and failing at 50%, so it does bite.

### detekt only covers `:app`

Spotless runs on every module. detekt does not, because enabling it across the six library modules reports **273 violations**. Either baseline them per module and gate new code from there, or work the list down. `baseline` currently points at a single shared `configs/detekt/detekt-baseline.xml`; whether one file can serve every module or each needs its own is untested.

### The screenshot suite covers two previews

`:app:validateDebugScreenshotTest` is a real gate: two reference images are committed under `app/src/screenshotTestDebug/reference/`, and re-rendering them on a different machine reproduces them byte for byte.

What it does not cover is most of the app. There is one preview in `CardScreenshotTests` and one in `SolPanScreenshotTests`, against roughly 900 lines of UI code sitting at 0% test coverage. The About screen, the tutorial overlay and the alignment visualiser all render unchecked. Add previews and run `:app:updateDebugScreenshotTest` to widen it.

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

### Version numbering is inconsistent

`.release-please-manifest.json` and `gradle.properties` both say `1.0.0`, but the newest tag is `v0.1.1` and no `v1.0.0` exists. The first release-please run therefore proposes 1.1.0 with a changelog covering the whole project history. Resolve by accepting that as a one-off backfill, curating it, or tagging `v1.0.0` on an earlier commit.

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

The release train works end to end: release-please opens the PR, merging it tags, and `release.yml` builds, signs, attests provenance and uploads to the Play track implied by the tag. What is missing is any check between tagging and publishing, so a broken build reaches a track before anyone looks at it.

### Wider device coverage

The app targets phones and tablets. Foldables, and the large-screen posture changes that come with them, are untested.
