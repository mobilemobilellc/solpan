# Security and Privacy

What SolPan collects, where it goes, and what protects it. This describes the app as built. Open decisions are in [ROADMAP.md](ROADMAP.md).

## Reporting a vulnerability

Open a [security advisory](https://github.com/mobilemobilellc/solpan/security/advisories/new) rather than a public issue.

## What the app handles

| Data | Source | Stored | Leaves the device |
|---|---|---|---|
| GPS coordinates | fused location provider | no, session memory only | no |
| Accelerometer and magnetometer | device sensors | no | no |
| Magnetic declination | computed from location | no | no |
| Tutorial-seen flag | user | yes, DataStore | no |

Location is the only sensitive item, and it is never written to disk. It is read while the screen is open, combined into `OptimalPanelParameters`, and discarded.

`UserPreferencesRepository` persists exactly one key, `tutorialSeen`, a boolean. Tilt mode is held in the ViewModel and is not persisted.

## Permissions

The manifest declares two:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

Location is load-bearing: panel azimuth and tilt cannot be computed without it. The app degrades to a "waiting for location" state rather than failing when permission is refused.

`INTERNET` is not declared by the app. Firebase and Google Play Services reach the network through their own merged manifest entries.

Nothing requests camera, contacts, storage or microphone.

## Storage

Preferences use plain `preferencesDataStore`. **Nothing is encrypted at rest.** The protection is Android's app-private storage, which keeps other apps out on a non-rooted device. That is proportionate to one boolean, but it means nothing sensitive may be added to DataStore without adding encryption first.

Backup is on:

```xml
android:allowBackup="true"
android:dataExtractionRules="@xml/data_extraction_rules"
android:fullBackupContent="@xml/backup_rules"
```

Both rule files are still the unmodified Android Studio templates, with their contents commented out, so no include or exclude rules are actually in force and the platform default applies. Since the only persisted value is `tutorialSeen`, nothing sensitive is exposed by this. Whether to scope the rules or disable backup is an open decision.

## Network

The app makes no direct HTTP calls. Solar position comes from Commons Suncalc, computed offline. The only traffic is Firebase Analytics and Crashlytics, over Google's own transport.

`android:usesCleartextTraffic` is not set. On API 28 and above the platform default is false. `minSdk` is 26, so on API 26 and 27 cleartext would be permitted; nothing in the app makes such a request.

There is no `network_security_config.xml` and no certificate pinning, because there is no first-party endpoint to pin.

## Analytics

Every event goes through the `AnalyticsTracker` interface, which is the single place to add redaction. Three events are defined:

- `logTutorialStarted`
- `logTutorialEnded`
- `logPermissionResult(granted: Boolean)`

No coordinates, no device identifiers beyond Firebase's own instance ID, and no user ID are sent. `FirebaseAnalyticsTracker` is the only implementation that talks to Firebase; tests use `FakeAnalyticsTracker`.

When logging around location, log the outcome and not the value:

```kotlin
// leaks coordinates into Crashlytics
Log.e("SolPan", "Failed with location: $location")

// says what happened without the data
Log.e("SolPan", "Location processing failed")
```

## Release builds

R8 is on for release (`isMinifyEnabled = true`) with `proguard-android-optimize.txt` plus `app/proguard-rules.pro`. The project rules are narrow, covering kotlinx-serialization only:

```proguard
-dontwarn kotlinx.serialization.internal.CommonEnumSerializer
-dontwarn edu.umd.cs.findbugs.annotations.**
-keepnames class kotlinx.serialization.internal.*Serializer* { <init>(...); }
```

Minification raises the cost of casual reverse engineering. It is not a security control and should not be described as one.

Release artifacts get build provenance attestation via `actions/attest-build-provenance` in `release.yml`.

## Dependencies

Versions are pinned in `gradle/libs.versions.toml` and moved by Renovate, so they are not duplicated here. GitHub Dependabot alerts are enabled.

There are currently **18 open alerts**, 1 critical and 7 high, in netty, bouncycastle, jackson and wire. These appear to come from the Gradle build classpath surfaced by dependency submission rather than from anything shipped in the APK, but which configuration they sit on has not been verified. Treat that as unconfirmed until it is.

Static analysis runs on every build. detekt and spotless both cover all seven modules, and every module's detekt findings are uploaded as SARIF to the Security tab. There is no detekt baseline: a finding is fixed, or the rule is disabled in `detekt.yml` with a stated reason.
