# Testing

How SolPan is tested, what that currently covers, and where the holes are. For the plan to close them see [ROADMAP.md](ROADMAP.md).

## What exists

| Source set | Files | Runs | What it does |
|---|---|---|---|
| `app/src/test` | 9 | CI, every push and PR | 31 JVM unit tests plus three fakes |
| `app/src/androidTest` | 2 | never in CI | instrumented tests, need a device |
| `app/src/screenshotTest` | 2 | CI, every push and PR | 4 `@Preview` composables, currently with no references to compare against |
| `baselineprofile/` | 2 | never in CI | macrobenchmarks, need a physical device |

## Unit tests

Plain JUnit with `kotlinx-coroutines-test`. No mocking framework: collaborators are hand-written fakes (`FakeAnalyticsTracker`, `FakeUserPreferencesRepository`, `FakeMagneticDeclinationProvider`), which is why the `AnalyticsTracker` and repository interfaces exist.

```bash
./gradlew :app:testDebugUnitTest
```

The tests live in `:app` but exercise code in `core` and `feature`. That is a known wrinkle, not a design: see [ROADMAP.md](ROADMAP.md).

What is covered:

- `SolarCalculatorTest` - sun position maths, the part most worth having tests for
- `AlignmentStateTest` - alignment thresholds
- `SolPanViewModelTest` - state emission across tilt modes
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

Current: **12.8% line coverage**, 192 of 1500 lines. `solpan.model` is at 100%, `solpan.optimizer` at 68%, and every `solpan.ui.*` package at 0%.

Nothing enforces a minimum. A drop will be visible in the PR comment and will not fail the build.

## Screenshot tests

Compose screenshot testing, four `@Preview` composables across `CardScreenshotTests` and `SolPanScreenshotTests`.

```bash
# compare against committed references
./gradlew :app:validateDebugScreenshotTest

# accept the current rendering after an intentional UI change
./gradlew :app:updateDebugScreenshotTest
```

**No reference images are committed**, so `validateDebugScreenshotTest` currently passes without comparing anything. Run the update task and commit `app/src/screenshotTest/reference/` to turn it into a real gate.

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
./gradlew app:detekt spotlessCheck :app:testDebugUnitTest :app:assembleDebug app:jacocoTestReport
./gradlew :app:validateDebugScreenshotTest
```

`Merge Queue Checks` runs the same set minus coverage when a pull request is queued. Note that `detekt` is `app:`-scoped in both while `spotlessCheck` is repo-wide; that asymmetry is deliberate and explained in [ROADMAP.md](ROADMAP.md).

Test results are published through `mikepenz/action-junit-report`, and detekt findings are uploaded as SARIF so they appear in the Security tab.
