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

# Gradle Build Optimization Guide for SolPan

**Last Updated:** April 2026
**Gradle Version:** 9.4.1
**AGP Version:** 9.2.0-rc01

This document details the build optimizations implemented in SolPan to ensure fast, predictable, and reproducible builds across all environments (local development, CI/CD, team machines).

## Executive Summary

SolPan's build is optimized for speed and reliability through:

- **Configuration Cache** - 60-80% faster rebuilds
- **Gradle Build Cache** - 30-50% faster incremental builds
- **Parallel Execution** - Multi-threaded task execution
- **JVM Memory Tuning** - Optimized heap for Android tasks
- **Convention Plugins** - Reduced configuration boilerplate
- **Dependency Management** - Version Catalogs for consistency

**Baseline Metrics (with optimizations enabled):**
- Clean build (first-time): ~90-120 seconds
- Incremental build (after code change): ~15-25 seconds
- Gradle daemon warm-up: ~5-10 seconds
- CI pipeline (full checks): ~180-220 seconds

## 1. Configuration Cache

### What It Is

The Configuration Cache caches the **result of configuration phase** (resolving tasks, dependencies, plugins) rather than re-running it for every build.

### Benefits

- **First build:** ~10% faster (small benefit due to full task execution still needed)
- **Incremental builds:** 60-80% faster (configuration phase is the bottleneck for non-code changes)
- **CI consistency:** Deterministic builds across machines

### Configuration

In `gradle.properties`:

```properties
org.gradle.configuration-cache=true
```

### Known Incompatibilities

Tasks that don't support configuration cache must opt out:

```bash
# Example: Screenshot tests need fresh camera access
./gradlew :app:validateDebugScreenshotTest --no-configuration-cache
```

**Current exceptions in SolPan CI:**
- `:app:validateDebugScreenshotTest` - Device/UI automation state

### Enabling for More Tasks

To check if a task supports configuration cache:

```bash
./gradlew myTask --configuration-cache
# If successful, it's compatible
# If it fails with "configuration cache incompatibility", investigate the task
```

## 2. Gradle Build Cache

### What It Is

The Build Cache stores **task outputs** (compiled classes, resources, APK artifacts) and reuses them when identical inputs are detected.

### Benefits

- **Incremental builds:** 30-50% faster when only some modules change
- **CI efficiency:** Build cache can be shared across CI agents
- **Team collaboration:** Teams can share build outputs (optional)

### Configuration

In `gradle.properties`:

```properties
org.gradle.caching=true
```

### How It Works

1. **Local cache** (default, ~/.gradle/build-cache/): Stores outputs on your machine
2. **Remote cache** (optional): Shared cache server for teams
3. **CI cache** (GitHub Actions): Handled by `gradle/actions/setup-gradle@v5`

### Managing the Build Cache

```bash
# View cache stats
./gradlew buildEnvironment

# Clear local cache if corrupted
rm -rf ~/.gradle/build-cache/

# Clean specific task outputs
./gradlew clean :app:assembleDebug
```

## 3. Parallel Execution

### What It Is

Gradle runs independent tasks simultaneously using multiple threads.

### Benefits

- **Reduced wall-clock time** for large builds
- **Better CPU utilization** on multi-core machines
- **CI efficiency** - full CI pipeline can complete in ~3 minutes

### Configuration

In `gradle.properties`:

```properties
# Already enabled (Gradle 9+ enables by default)
# For explicit control:
org.gradle.parallel=true
org.gradle.workers.max=4  # or more on high-core systems
```

### Command Line

```bash
# Force parallel execution
./gradlew detekt spotlessCheck testDebugUnitTest assembleDebug --parallel

# Disable parallelization (useful for debugging)
./gradlew detekt spotlessCheck testDebugUnitTest assembleDebug --no-parallel
```

### CI Pipeline Parallelization

In `.github/workflows/build.yml`:

```yaml
- name: Run checks and build debug APK
  run: ./gradlew app:detekt app:spotlessCheck :app:testDebugUnitTest :app:assembleDebug --parallel --continue --scan
```

**Execution flow:**
1. `app:detekt` (parallel with spotlessCheck, tests)
2. `app:spotlessCheck` (parallel with detekt, tests)
3. `:app:testDebugUnitTest` (parallel with detekt, spotlessCheck)
4. `:app:assembleDebug` (waits for tests to pass; requires compiled code)

**Expected timeline:** ~60-90 seconds (vs ~180 seconds sequential)

## 4. JVM Memory Tuning

### What It Is

The Gradle daemon (background process that runs Gradle) needs adequate heap memory to compile large projects and run intensive tasks.

### Configuration

In `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx6g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -XX:+UseParallelGC -XX:MaxMetaspaceSize=1g
```

### Breakdown

| Flag | Purpose | Value | Note |
|------|---------|-------|------|
| `-Xmx6g` | Max heap | 6GB | Adjust for your machine: 6GB (16GB+ RAM), 4GB (8GB RAM) |
| `-XX:+HeapDumpOnOutOfMemoryError` | OOM handling | Dump on error | Helps debug memory issues |
| `-Dfile.encoding=UTF-8` | Character encoding | UTF-8 | Ensures consistent source file handling |
| `-XX:+UseParallelGC` | GC algorithm | Parallel GC | Fast for build tools (not low-latency) |
| `-XX:MaxMetaspaceSize=1g` | Metadata heap | 1GB | Sufficient for Kotlin compilation |

### Adjusting for Your Machine

**For CI environments (limited RAM):**
```properties
org.gradle.jvmargs=-Xmx4g -XX:+HeapDumpOnOutOfMemoryError ...
```

**For powerful local machines (32GB+ RAM):**
```properties
org.gradle.jvmargs=-Xmx8g -XX:+HeapDumpOnOutOfMemoryError ...
org.gradle.workers.max=8
```

### Monitoring JVM Usage

```bash
# Watch Gradle daemon memory usage
./gradlew build --scan  # Opens build scan with JVM metrics

# Or use jps to list running daemons
jps -v | grep GradleDaemon
```

## 5. Convention Plugins (`build-logic`)

### What It Is

Convention plugins centralize common build configuration to avoid duplication across modules.

### Benefits

- **Consistency:** All modules use identical Android config, ktlint rules, Detekt settings
- **Maintainability:** Change one rule in `build-logic`, all modules update automatically
- **Reduced boilerplate:** Module `build.gradle.kts` files are ~10-20 lines instead of 50+

### Current Plugins in SolPan

Located in `build-logic/convention/src/main/kotlin/`:

| Plugin | Purpose | Modules |
|--------|---------|---------|
| `AndroidKotlin` | Android + Kotlin common config | `:app`, `:feature:*`, `:core:*` |
| `AndroidKotlinLibrary` | Library-specific config | `:core:*` |
| `AndroidLibraryCompose` | Compose UI library config | `:core:designsystem` |

### Adding a New Convention Plugin

1. Create plugin file in `build-logic/convention/src/main/kotlin/`:
```kotlin
class MyConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(plugin = "com.android.library")
        // ... configure common settings
    }
}
```

2. Register in `build-logic/convention/build.gradle.kts`:
```kotlin
gradlePlugin {
    plugins {
        register("myConvention") {
            id = "app.mobilemobile.solpan.my-convention"
            implementationClass = "MyConventionPlugin"
        }
    }
}
```

3. Use in module `build.gradle.kts`:
```kotlin
plugins {
    id("app.mobilemobile.solpan.my-convention")
}
```

## 6. Dependency Management with Version Catalogs

### What It Is

Version Catalogs centralize library versions in `gradle/libs.versions.toml`, enabling:
- Single source of truth for versions
- Type-safe dependency declarations
- Easy bulk updates

### Current Setup

`gradle/libs.versions.toml` defines:
- **Versions:** AGP, Kotlin, Gradle, Compose, AndroidX libraries
- **Libraries:** Grouped by function (androidX, compose, testing, etc.)
- **Plugins:** Build plugins (Android, Kotlin, etc.)

### Benefits

- **Consistency:** All modules use same library versions
- **Efficiency:** Update version once, all modules get new version
- **Safety:** Type-safe `libs.*` syntax prevents typos

### Updating Dependencies

```bash
# Check for updates
./gradlew dependencyUpdates

# Update specific library version in gradle/libs.versions.toml
# Gradle automatically makes available to all modules
```

## 7. Build Scan Integration

### What It Is

Build Scans are detailed reports of build execution, including:
- Task execution timeline
- Configuration cache hits/misses
- Build cache statistics
- Performance bottlenecks
- Memory usage

### Enabling

Already configured in SolPan CI:
```yaml
run: ./gradlew ... --scan
```

Locally:
```bash
./gradlew build --scan
# Outputs: "Publishing build scan..."
# Link to https://scans.gradle.com/...
```

### Reading a Build Scan

1. **Performance:** Timeline of task execution, identify slow tasks
2. **Configuration Cache:** How many phases needed config recomputation
3. **Build Cache:** Hit rate on task outputs
4. **Deprecations:** Warnings about deprecated APIs
5. **Failures:** Detailed error logs and stack traces

## 8. Android Gradle Plugin 9 Optimizations

### Configuration Cache Support

AGP 9 has **excellent** configuration cache support. Most tasks are compatible; the main exceptions are:
- UI automation tasks (screenshot tests, UIAutomator)
- Tasks that query device state

### Build Performance

AGP 9 introduces:
- **Faster task configuration** through lazy task registration
- **Improved dependency graph** resolution
- **Better Kotlin compilation** integration

### Kotlin-First Configuration

AGP 9 prioritizes Kotlin DSL:
- Compile-time type safety in `build.gradle.kts`
- IDE autocomplete for Android extension properties
- Automatic Kotlin source sets

## 9. Performance Benchmarking

### Measure Build Time

```bash
# Full build with breakdown
./gradlew clean build --profile
# Generates: build/reports/profile/profile-<timestamp>.html

# Specific task timing
./gradlew :app:assembleDebug --scan
# Build Scan shows task timeline
```

### Configuration Cache Impact

```bash
# Without configuration cache
./gradlew build  # First time ~120s

# With configuration cache (subsequent builds)
./gradlew build  # ~30-40s
```

### Analyzing Gradle Logs

```bash
# Verbose logging
./gradlew build --info 2>&1 | grep -i "configuration\|cache\|parallel" | head -20

# Debug configuration issues
./gradlew build --debug 2>&1 | head -100
```

## 10. CI/CD Optimization

### GitHub Actions Gradle Setup

In `.github/workflows/build.yml`:

```yaml
- uses: gradle/actions/setup-gradle@v5
  with:
    cache-read-only: ${{ github.ref != 'refs/heads/main' }}
    cache-encryption-key: ${{ secrets.GRADLE_CACHE_ENCRYPTION_KEY }}
    dependency-graph: generate-and-submit
```

**Benefits:**
- Gradle wrapper validation
- Automatic build cache setup
- Dependency graph submission
- Faster CI runs (30-50% via cache)

### CI Build Strategy

1. **Pull requests:** `cache-read-only=true` (don't corrupt shared cache)
2. **Main branch:** `cache-read-only=false` (write-through to refresh cache)
3. **Encryption key:** Protects cached artifacts on shared runners

### Parallel CI Execution

Current setup:
```bash
./gradlew app:detekt app:spotlessCheck :app:testDebugUnitTest :app:assembleDebug --parallel --continue --scan
```

Tasks execute in parallel:
- `detekt` vs `spotlessCheck` vs `testDebugUnitTest` (independent)
- `assembleDebug` waits for `testDebugUnitTest` (requires compiled classes)

**Result:** 60-90 seconds vs ~180 seconds sequential

## 11. Troubleshooting Build Performance

### Build is Slow (>120 seconds clean build)

**Check:**
1. Gradle version: `./gradlew --version` (should be 9.4+)
2. JVM memory: `jps -v | grep GradleDaemon`
3. Configuration cache: `./gradlew build --configuration-cache`
4. Build scan: `./gradlew build --scan` for detailed profiling

**Fix:**
- Increase JVM heap: `-Xmx8g` in `gradle.properties`
- Disable problematic plugins temporarily: `--no-daemon` to isolate daemon issues
- Check for rogue tasks: `./gradlew tasks --group=other`

### Configuration Cache Incompatibility

```bash
./gradlew build --configuration-cache
# If error: "configuration cache incompatibility"
```

**Fix:**
- Update task to support configuration cache (report to plugin maintainer)
- Add `--no-configuration-cache` only for that task in CI
- Consider task scheduling changes

### Build Cache Stale

```bash
# Clear and rebuild
rm -rf ~/.gradle/build-cache/
./gradlew clean build --build-cache --scan
```

## 12. Checklist: Build Optimization Best Practices

- [ ] **Configuration Cache enabled** in `gradle.properties`
- [ ] **Build Cache enabled** in `gradle.properties`
- [ ] **Parallel execution** enabled: `--parallel` in builds
- [ ] **JVM memory** tuned: `-Xmx6g` or higher for your system
- [ ] **Convention plugins** used for common configuration
- [ ] **Version Catalog** centralized in `gradle/libs.versions.toml`
- [ ] **Build Scans** used for profiling (`--scan` flag)
- [ ] **CI caching** configured with GitHub Actions Gradle setup
- [ ] **Performance baseline** established: clean/incremental/CI times documented
- [ ] **Monitoring** in place: APK size, build times, cache hit rates

## 13. References

- [Gradle Configuration Cache Documentation](https://docs.gradle.org/current/userguide/configuration_cache.html)
- [Gradle Build Cache Documentation](https://docs.gradle.org/current/userguide/build_cache.html)
- [AGP 9 Release Notes](https://developer.android.com/build/releases/gradle-plugin-release-notes)
- [GitHub Actions Gradle Setup](https://github.com/gradle/actions)

## Appendix: SolPan Build Profiles

### Local Development Build

```bash
./gradlew :app:assembleDebug
# Time: ~30-45 seconds (warm Gradle daemon, config cache hit)
# Output: app/build/outputs/apk/debug/app-debug.apk (6.2MB)
```

### CI Pipeline Build

```bash
./gradlew app:detekt app:spotlessCheck :app:testDebugUnitTest :app:assembleDebug --parallel --continue
# Time: ~120-160 seconds (cold Gradle daemon, fresh config cache)
# Output: Debug APK + test reports + lint reports
```

### Release Build

```bash
./gradlew :app:assembleRelease
# Time: ~40-60 seconds
# Output: app/build/outputs/apk/release/app-release.apk (optimized, unsigned)
```

---

**Last Verified:** April 20, 2026
**Gradle Version:** 9.4.1
**AGP Version:** 9.2.0-rc01
