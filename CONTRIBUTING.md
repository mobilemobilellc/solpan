# Contributing to SolPan

Contributions are welcome. [ROADMAP.md](ROADMAP.md) lists work that is already known to need doing, if you want somewhere to start.

## Reporting bugs

Open an issue with a clear title, steps to reproduce, the app version, your device and Android version, and any logs or screenshots.

## Suggesting enhancements

Open an issue describing the feature and why it is worth having. Mockups help.

## Pull requests

1. Fork and branch off `main`.
2. Make your change.
3. Format everything: `./gradlew spotlessApply`
4. Run what CI runs:

```bash
./gradlew detekt spotlessCheck testDebugUnitTest :app:assembleDebug --parallel
```

5. If you changed UI on purpose, regenerate the references and commit them:

```bash
./gradlew :app:updateDebugScreenshotTest
```

6. Open the pull request. CI runs the same checks, comments the coverage figure, and the merge queue re-runs them before anything lands on `main`.

Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/); release-please builds the changelog and version bump from them. See [RELEASE.md](RELEASE.md).

## Project structure

| Module | Holds |
|---|---|
| `:app` | UI, entry point, and for now every test |
| `:feature:optimizer` | `SolPanViewModel` and the optimisation logic |
| `:core:model` | `TiltMode`, `LocationData`, `OptimalPanelParameters`, alignment state |
| `:core:data` | repository implementations, DataStore access |
| `:core:analytics` | the `AnalyticsTracker` seam and its Firebase implementation |
| `:core:designsystem` | shared composables, theme, design tokens |
| `:core:solar` | `SolarCalculator` |
| `:baselineprofile` | macrobenchmarks and the profile generator |
| `build-logic/` | Gradle convention plugins |

[ARCHITECTURE.md](ARCHITECTURE.md) covers how they fit together.

## Build system

AGP 9.4.1 on Gradle 9.7.1 and Kotlin 2.4.20, with the configuration cache and build cache both on. Shared configuration lives in `build-logic/` rather than being repeated per module:

| Plugin | Applies |
|---|---|
| `solpan.android.application` | app defaults, `compileSdk`/`compileSdkMinor`, R8 on release, spotless |
| `solpan.android.library` | library defaults, Dokka, spotless |
| `solpan.jacoco.report` | the aggregate coverage report |

Dependency versions are pinned in `gradle/libs.versions.toml`. Renovate raises the bumps; group a toolchain change (Gradle and AGP move together) into one pull request, because neither passes alone.

## Commands

```bash
# build
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease

# tests
./gradlew testDebugUnitTest
./gradlew :feature:optimizer:testDebugUnitTest --tests "*SolPanViewModelTest"
./gradlew app:jacocoTestReport
./gradlew app:jacocoCoverageVerification

# quality
./gradlew spotlessApply          # every module
./gradlew spotlessCheck          # every module
./gradlew detekt                 # every module

# docs
./gradlew :dokkaGenerate         # build/dokka/html
```

## Code style

Spotless runs ktfmt then ktlint 1.8.0 with the Compose rule set, configured once in `build-logic` and applied to every module. Detekt uses `configs/detekt/detekt.yml` and runs on every module too.

There is no detekt baseline. A finding gets fixed, or the rule gets turned off in `detekt.yml` with a comment saying why. Two are currently off: the `formatting` ruleset, because spotless owns formatting and detekt's bundled ktlint is older and disagrees with it, and `LongMethod` for `@Composable`, because a long composable is usually a large UI tree rather than a complex function. `CyclomaticComplexMethod` still applies to composables.

- Apache 2.0 licence headers are required on Kotlin files and are added by `spotlessApply`.
- No star imports.
- No dependency injection framework. Dependencies are constructor-injected and wired at the call site, which is what keeps the fakes in tests simple.
- Platform types get an interface in front of them (`MagneticDeclinationProvider` over `GeomagneticField`) so they can be faked.

## Documentation

Public declarations carry KDoc. Dokka 2 publishes it to <https://mobilemobilellc.github.io/solpan/> on every push to `main`.

- Document what a thing does and any non-obvious invariant, not how it is implemented.
- `@param` and `@return` where the name does not already say it.
- Link with `[Type]` so Dokka resolves it.
- A file-level comment that belongs to no declaration must be `/* */`, not `/** */`; ktlint rejects a dangling top-level KDoc.

Build it locally with `./gradlew :dokkaGenerate` and open `build/dokka/html/index.html`.

## Key classes

- `app.mobilemobile.solpan.optimizer.SolPanViewModel` - the state container
- `app.mobilemobile.solpan.solar.SolarCalculator` - sun position
- `app.mobilemobile.solpan.orientation.DeviceOrientationController` - accelerometer and magnetometer fusion
- `app.mobilemobile.solpan.location.DeviceLocationManager` - location updates
- `app.mobilemobile.solpan.ui.screen.SolPanScreen` - the main screen
- `app.mobilemobile.solpan.designsystem.theme.SolPanTheme` - theming

## Questions

Open a GitHub Discussion, or check existing issues.
