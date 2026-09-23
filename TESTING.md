# Testing

How SolPan is tested, what that currently covers, and where the holes are. For the plan to close them see [ROADMAP.md](ROADMAP.md).

## What exists

| Source set | Runs | What it does |
|---|---|---|
| `core/solar/src/test` | CI, every push and PR | 12 tests: solar position and alignment |
| `core/data/src/test` | CI, every push and PR | 2 tests: preferences, including an unreadable file |
| `feature/optimizer/src/test` | CI, every push and PR | 10 tests: ViewModel and tutorial flow, plus three fakes |
| `app/src/test` | CI, every push and PR | 14 tests: locale-aware formatting and orientation readings |
| `app/src/screenshotTest` | CI, every push and PR | 2 previews, compared against committed references |
| `app/src/androidTest` | never in CI | instrumented tests, need a device |
| `baselineprofile/` | never in CI | macrobenchmarks, need a physical device |

38 unit tests in total.

## Unit tests

Plain JUnit with `kotlinx-coroutines-test`. No mocking framework: collaborators are hand-written fakes (`FakeAnalyticsTracker`, `FakeUserPreferencesRepository`, `FakeMagneticDeclinationProvider`), which is why the `AnalyticsTracker` and repository interfaces exist.

```bash
./gradlew testDebugUnitTest
```

Each test lives in the module it covers, so a module's tests run when that module changes.

What is covered:

- `SolarCalculatorTest` - sun position maths, the part most worth having tests for
- `AlignmentStateTest` - alignment thresholds
- `SolPanViewModelTest` - state emission across tilt modes, and REALTIME against a known sun position
- `DataStoreUserPreferencesRepositoryTest` - the tutorial flag round trip, and defaults from a corrupt file
- `DeviceOrientationControllerTest` - converting sensor angles, and dropping readings that are not finite
- `TutorialFlowTest` - first-run overlay and its persistence
- `FormattingExtensionsTest` - locale-aware number and angle formatting

## Coverage

JaCoCo, via the `solpan.jacoco.report` convention plugin. The report spans every module except `baselineprofile`, because the tests in `:app` cover code that lives elsewhere.

```bash
./gradlew app:jacocoTestReport
# app/build/reports/jacoco/html/index.html
# app/build/reports/jacoco/jacoco.csv
```

CI runs the same task and comments the total on every pull request.

Current: **15.02% line coverage**, 235 of 1565 lines. `solpan.model` and `solpan.solar` are at 100%, `solpan.data` at 86%, `solpan.optimizer` at 71%, and every `solpan.ui.*` package at 0%.

`app:jacocoCoverageVerification` fails the build below **12%**, which CI runs on every pull request. It is a ratchet against regression rather than a target: see [ROADMAP.md](ROADMAP.md).

## Screenshot tests

Compose screenshot testing, two `@Preview` composables across `CardScreenshotTests` and `SolPanScreenshotTests`.

```bash
# compare against committed references
./gradlew :app:validateDebugScreenshotTest

# accept the current rendering after an intentional UI change
./gradlew :app:updateDebugScreenshotTest
```

Two reference images are committed under `app/src/screenshotTestDebug/reference/`, one per preview, and the comparison is real: re-rendering on a different machine reproduces them byte for byte. Coverage is the limit rather than the mechanism, so adding previews is what widens it.

## Instrumented tests

`SolPanScreenE2ETest` drives the main screen through Compose UI testing. It needs a connected device or emulator and is not part of any workflow:

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Benchmarks

`baselineprofile/` holds `SolPanStartupBenchmark` and `SolPanCriticalFlowBenchmark`. Both need a physical device and neither runs in CI. See [PERFORMANCE.md](PERFORMANCE.md).

## What CI runs

`Build and Test` on every push to `main` and every pull request:

```bash
./gradlew detekt spotlessCheck testDebugUnitTest :app:assembleDebug app:jacocoTestReport app:jacocoCoverageVerification
./gradlew :app:validateDebugScreenshotTest
```

`Merge Queue Checks` runs the same set minus coverage when a pull request is queued. Everything except the screenshot and APK tasks is unscoped, so it covers all seven modules.

Test results are published through `mikepenz/action-junit-report`, and detekt findings are uploaded as SARIF so they appear in the Security tab.
