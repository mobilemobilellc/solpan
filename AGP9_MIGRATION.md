# AGP 9 Migration Guide for SolPan

This document outlines the migration from Android Gradle Plugin 8.x to AGP 9.0 and the modernization efforts completed for the SolPan project.

## Overview

SolPan has been successfully migrated to **Android Gradle Plugin 9.0** with a multi-module architecture. AGP 9 introduces significant improvements including improved performance, Kotlin-first configuration, and stricter type safety.

## Migration Highlights

### 1. AGP 9 Build System

**Before (AGP 8.x):**
```kotlin
// build.gradle.kts with boilerplate
android {
    compileSdk = 33
    defaultConfig {
        applicationId = "app.mobilemobile.solpan"
        minSdk = 26
        targetSdk = 33
    }
}
```

**After (AGP 9.0):**
```kotlin
// Convention plugin handles most setup
id("solpan.android.application") // or "solpan.android.library"
```

### 2. Gradle Version Update

- Requires **Gradle 9.1.0+** for AGP 9 compatibility
- Configuration cache enabled by default (performance improvement)
- New dependency resolution model

### 3. Kotlin Compatibility

- Requires **Kotlin 2.3.20+** for full AGP 9 support
- Kotlin version now managed via version catalog (`gradle/libs.versions.toml`)
- JDK 21 toolchain requirement (with JDK 17 support in CI)

### 4. Stricter Type System in DSL

**Major Challenge:** AGP 9 enforces strict typing on extension classes.

**Problem:**
```kotlin
// ❌ Doesn't work in AGP 9
fun configureKotlinAndroid(extension: CommonExtension<*, *, *, *, *>) {
    // extension doesn't expose all ApplicationExtension OR LibraryExtension members
}
```

**Solution:**
Split into application-specific and library-specific functions:

```kotlin
// ✅ AGP 9 compatible
fun configureKotlinAndroidApp(extension: ApplicationExtension) {
    // Full access to ApplicationExtension members
}

fun configureKotlinAndroidLibrary(extension: LibraryExtension) {
    // Full access to LibraryExtension members
}
```

**Files Changed:**
- `build-logic/convention/src/main/kotlin/AndroidKotlin.kt`
- `build-logic/convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt`
- `build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt`

### 5. Multi-Module Architecture

SolPan now uses a clean, modular structure:

```
solpan/
├── app/                      # Main application module
├── feature/
│   └── optimizer/           # Feature module with ViewModel
├── core/
│   ├── model/              # Data models
│   ├── data/               # Repositories
│   ├── analytics/          # Analytics abstraction
│   ├── designsystem/       # UI components & theming
│   └── solar/              # Solar calculations
└── build-logic/            # Convention plugins
```

**Benefits:**
- Clear separation of concerns
- Faster incremental builds
- Better code isolation
- Easier to test and maintain

### 6. Key Dependency Injection Pattern

**Before:** Centralized DI or heavyweight frameworks

**After:** Constructor injection + Compose remembrance:

```kotlin
@Composable
fun rememberDeviceLocationController(
    locationManager: DeviceLocationManager = remember { DeviceLocationManager() }
): DeviceLocationController = remember(locationManager) { ... }
```

**Advantage:** Lightweight, type-safe, and testable

## Breaking Changes and Fixes

### 1. GeomagneticField Unit Test Issue

**Problem:** `GeomagneticField` is an Android native API that fails in JVM unit tests.

**Symptom:**
```
java.lang.RuntimeException: Unknown magneticModel number
    at com.android.location.fused.GeomagneticField.java
```

**Solution:** Dependency injection with interface abstraction

```kotlin
interface MagneticDeclinationProvider {
    fun getDeclination(latitude: Double, longitude: Double, altitude: Double): Float?
}

class AndroidMagneticDeclinationProvider : MagneticDeclinationProvider {
    override fun getDeclination(lat: Double, lon: Double, alt: Double): Float? {
        return try {
            GeomagneticField(lat.toFloat(), lon.toFloat(), alt.toInt(), System.currentTimeMillis())
                .declination
        } catch (e: Exception) {
            null // Graceful fallback
        }
    }
}

class FakeMagneticDeclinationProvider : MagneticDeclinationProvider {
    override fun getDeclination(lat: Double, lon: Double, alt: Double) = 10f // Fixed for tests
}
```

**Usage in ViewModel:**
```kotlin
class SolPanViewModel(
    private val magneticDeclinationProvider: MagneticDeclinationProvider = 
        AndroidMagneticDeclinationProvider()
) {
    // Now testable!
}
```

### 2. Module Reorganization Import Paths

**Problem:** After moving classes between modules, many imports broke.

**Example:** `SolarPosition` moved from `ui/data/` to `core/model/`

```kotlin
// ❌ Old (broken after module split)
import app.mobilemobile.solpan.ui.data.SolarPosition

// ✅ New (correct module location)
import app.mobilemobile.solpan.core.model.SolarPosition
```

**Files Fixed:**
- `GuidanceComposables.kt` - Solar calculation imports
- `PermissionRequestCard.kt` - Removed external action strings
- `TargetParametersCard.kt` - Model imports
- `SolPanScreen.kt` - Multiple model and UI imports

### 3. Deprecated API Warnings

**Current Status:**
- ✅ Suppressed `currentWindowAdaptiveInfo` deprecation (V2 migration path noted for future)
- ✅ Removed unnecessary safe call on `MultiplePermissionsState`

**Future Migration:**
```kotlin
// Current (deprecated in newer versions)
val windowSizeClass = rememberWindowSizeClass()

// Migration path: Use currentWindowAdaptiveInfo V2 when available
// Expected in future Jetpack Compose release with L/XL width size classes
```

## Build Verification

All checks must pass before submitting PRs:

```bash
# Full CI check (same as GitHub Actions)
./gradlew app:detekt app:spotlessCheck :app:testDebugUnitTest :app:assembleDebug --parallel

# Results (as of latest commit):
# ✅ 34 unit tests passing
# ✅ Detekt lint passing (baseline: 0 issues)
# ✅ Spotless formatting passing
# ✅ Debug APK assembles successfully
```

## Convention Plugins

Convention plugins centralize common configuration:

**File:** `build-logic/convention/src/main/kotlin/AndroidKotlin.kt`

```kotlin
fun configureKotlinAndroidApp(extension: ApplicationExtension) {
    extension.apply {
        compileSdk = 37
        defaultConfig {
            minSdk = 26
        }
        compileOptions {
            targetCompatibility = JavaVersion.VERSION_21
            sourceCompatibility = JavaVersion.VERSION_21
        }
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}
```

**Plugin Usage:**
```kotlin
// app/build.gradle.kts
plugins {
    id("solpan.android.application")
}
```

## Firebase Integration

Firebase (Analytics, Crashlytics, Performance) is integrated at the `app` module level:

```kotlin
// app/build.gradle.kts
id("com.google.gms.google-services")
```

Configuration:
- `google-services.json` is **NOT committed** (decoded from CI secret)
- Firebase SDK versions managed in version catalog
- Analytics tracker abstracted via interface for testability

## Testing Strategy

### Unit Tests (JVM)
- Run with `./gradlew :app:testDebugUnitTest`
- Mock platform APIs (GeomagneticField, etc.) using dependency injection
- Use Fake implementations for integration test doubles

### Screenshot Tests
- Stored in `app/src/screenshotTest/kotlin/`
- Updated with `./gradlew :app:updateDebugScreenshotTest`
- Validated with `./gradlew :app:validateDebugScreenshotTest`
- Primarily used for UI regression detection

### Code Quality Checks
- **Detekt** static analysis (`./gradlew app:detekt`)
- **Spotless** formatting with ktlint (`./gradlew app:spotlessApply`)
- **License headers** enforced via Spotless (Apache 2.0)

## Modernization Checklist

- ✅ Updated to AGP 9.0
- ✅ Gradle 9.1.0+ compatibility
- ✅ Kotlin 2.3.20+ compatibility
- ✅ Multi-module architecture (8 modules)
- ✅ Convention plugins for shared config
- ✅ Fixed GeomagneticField unit test issues
- ✅ Updated all module import paths
- ✅ Verified all unit tests passing (34/34)
- ✅ Enhanced CONTRIBUTING.md with modernization details
- ⏳ Consider L/XL width size class support (future)
- ⏳ Review additional API deprecations in future Jetpack updates

## Resources

- [Android Gradle Plugin 9.0 Release Notes](https://developer.android.com/build/releases/gradle-plugin)
- [Kotlin 2.3 Release Notes](https://kotlinlang.org/docs/releases.html)
- [Gradle 9.0 Release Notes](https://docs.gradle.org/current/release-notes.html)
- [Android Modular Architecture Best Practices](https://developer.android.com/guide/modularization)

## Support

For questions or issues related to this migration:
1. Check existing GitHub Issues
2. Review the relevant module's README (if present)
3. Consult the Android documentation links above
4. Open a new issue with details about the problem
