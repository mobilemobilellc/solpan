# Performance

What the build does for performance, and how to measure whether it works. There are no committed baseline figures: nothing in CI measures startup, frame timing or memory, so any number written here would be someone's laptop on one afternoon. Measure before you optimise.

## What is in place

**R8 on release.** `isMinifyEnabled = true` and `isShrinkResources = true`, with `proguard-android-optimize.txt` plus `app/proguard-rules.pro`, which is otherwise the default template. A resource the code looks up by name rather than by `R` id is invisible to the shrinker and gets removed, so reference resources by id. Debug builds are unminified and are not a size or startup reference.

**Baseline Profile, wired but not generated.** `androidx.baselineprofile` is applied to `:app` and `:baselineprofile`, and `:app` consumes whatever `:baselineprofile` produces. Generation has not produced rules yet, so `app/src/main/baseline-prof.txt` is three comment lines and startup gets no benefit. The device-state gates that stop it are in [ROADMAP.md](ROADMAP.md).

**Compose defaults.** State is hoisted into one `StateFlow` per screen and collected with `collectAsStateWithLifecycle`, so recomposition stops when the app is backgrounded. The staggered grid keys its items, which is what keeps scrolling from recomposing the whole list.

## Measuring

### Startup and frame timing

The macrobenchmarks in `baselineprofile/` are the intended path, on a physical device:

```bash
./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
```

They do not yet yield a profile: the run stops at macrobenchmark's device-state checks. See [ROADMAP.md](ROADMAP.md).

For a quick read without the harness:

```bash
# cold start, in milliseconds
adb shell am start-activity -W -S app.mobilemobile.solpan/.SolPanActivity | grep TotalTime

# frame timing for the last run
adb shell dumpsys gfxinfo app.mobilemobile.solpan framestats
```

### Memory

```bash
adb shell dumpsys meminfo app.mobilemobile.solpan
```

### APK size

CI prints the debug APK size to the job summary on every run and warns above 24 MB. That is a debug APK and runs large; the release APK is the one that matters, and it is built only by the release workflow.

```bash
./gradlew :app:assembleRelease
ls -l app/build/outputs/apk/release/
```

### Where time goes in a build

```bash
./gradlew :app:assembleDebug --scan
```

Develocity is configured for this repo, so `--scan` gives a task-level timeline. The configuration cache and build cache are both on in `gradle.properties`; a build that reconfigures every time usually means something in a build script is reading state at configuration time.

## Things to avoid

- Reading sensors or location faster than the UI updates. `DeviceLocationManager` sets the request interval; raising it costs battery for no visible gain.
- Recomposing on raw sensor values. Orientation is debounced into `OrientationData` before it reaches state.
- Unkeyed items in the staggered grid, which makes every insert recompose the list.
- Allocating inside a composable body or a sensor callback. Both run at display rate.
- Doing solar maths per frame. `SolarCalculator` output changes on the order of minutes, and the realtime ticker refreshes it on an interval rather than continuously.

## In production

Crashlytics reports crashes and crash-free sessions from real devices, which is the only signal here that covers hardware the project does not own. Performance Monitoring is not included, so there is no field data on startup time.
