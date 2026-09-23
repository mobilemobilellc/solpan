# Roadmap

What is unfinished, and what is planned. Everything here has been checked against the build or the running app rather than assumed.

## Unfinished

### Tests sit in the wrong module

All 31 unit tests live in `:app`, but they exercise `SolarCalculator`, `TiltMode`, `DefaultLocationRepository`, `UserPreferencesRepository` and `SolPanViewModel`, which live in `core` and `feature`. The six library modules have no tests of their own.

Line coverage is 12.8%, 192 of 1500 lines:

| Package | Coverage |
|---|---|
| `solpan.model` | 100% |
| `solpan.optimizer` | 68% |
| every `solpan.ui.*` package | 0% |

Moving each test next to the code it covers is the prerequisite for per-module coverage gates meaning anything. `core:solar` holds the solar maths and is the clearest gap.

### No coverage gate

`app:jacocoTestReport` produces a report and CI comments the figure on every pull request, but nothing fails on a drop. There is no `jacocoTestCoverageVerification` and no `violationRules` in the build. Adding one needs a number chosen against the real 12.8%, not an aspirational one.

### detekt only covers `:app`

Spotless runs on every module. detekt does not, because enabling it across the six library modules reports **273 violations**. Either baseline them per module and gate new code from there, or work the list down. `baseline` currently points at a single shared `configs/detekt/detekt-baseline.xml`; whether one file can serve every module or each needs its own is untested.

### The screenshot suite covers two previews

`:app:validateDebugScreenshotTest` is a real gate: two reference images are committed under `app/src/screenshotTestDebug/reference/`, and re-rendering them on a different machine reproduces them byte for byte.

What it does not cover is most of the app. There is one preview in `CardScreenshotTests` and one in `SolPanScreenshotTests`, against roughly 900 lines of UI code sitting at 0% test coverage. The About screen, the tutorial overlay and the alignment visualiser all render unchecked. Add previews and run `:app:updateDebugScreenshotTest` to widen it.

### The Baseline Profile is not wired up

The feature is coded but never wired up. `baselineprofile/` holds `BaselineProfileGenerator`, `SolPanStartupBenchmark` and `SolPanCriticalFlowBenchmark`, and `app/src/main/baseline-prof.txt` is three comment lines with no rules.

The `androidx.baselineprofile` plugin is declared in the catalog and sits in the root build as `apply false`, but no module applies it. `baselineprofile/build.gradle.kts` applies only `com.android.test`. Consequences:

- `generateBaselineProfile` is not a real task, though `baseline-prof.txt` tells you to run it.
- `:app` would not consume a profile even if one existed.
- Neither benchmark runs in CI.

To finish it: apply `androidx.baselineprofile` in both `:app` and `:baselineprofile`, generate on a physical device, and commit the result.

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

Once tests move to their own modules, run bare `detekt spotlessCheck test` instead of the `app:`-scoped task list, and enforce coverage per module rather than in aggregate.

### Architecture decision records

A short `docs/adr/` set covering the choices already made: Navigation 3 over Navigation 2, DataStore over SharedPreferences, the `AnalyticsTracker` seam, and the module split itself.

### Release smoke check

The release train works end to end: release-please opens the PR, merging it tags, and `release.yml` builds, signs, attests provenance and uploads to the Play track implied by the tag. What is missing is any check between tagging and publishing, so a broken build reaches a track before anyone looks at it.

### Wider device coverage

The app targets phones and tablets. Foldables, and the large-screen posture changes that come with them, are untested.
