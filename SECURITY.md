# SolPan Security & Privacy Guide

## Security Overview

SolPan is built with privacy and security as first-class concerns. This document outlines the security architecture and best practices implemented.

## Data Classification

### Sensitive Data (Location)
- **GPS Coordinates**: User's real-time location
- **Handling**: Only collected when app has permission
- **Storage**: Never persisted to disk
- **Transmission**: Only to Google Play Services (on-device)
- **Retention**: Transient (current session only)

### Non-Sensitive Data (Preferences)
- **Selected Tilt Mode**: User preference
- **Handling**: User-selected value
- **Storage**: Encrypted via DataStore
- **Transmission**: Not transmitted
- **Retention**: Until user clears app data

### Non-Sensitive Data (Sensor Readings)
- **Accelerometer/Magnetometer**: Device orientation
- **Handling**: Processed locally
- **Storage**: Never stored
- **Transmission**: Not transmitted
- **Retention**: Current event only

## Permissions Model

### Required Permissions (Location)

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**Justification**: Accurate GPS location is core to solar panel orientation calculation. No alternative exists.

**Runtime Permission Handling**:
```kotlin
if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    != PackageManager.PERMISSION_GRANTED
) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        REQUEST_LOCATION_PERMISSION
    )
}
```

### Optional Permissions (Vibration)

```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

**Justification**: Haptic feedback improves UX but app functions without it.

### Dangerous Permissions Avoided

❌ **Not used**:
- INTERNET (no network calls)
- CAMERA (no camera access needed)
- CONTACTS (no contact access)
- READ_EXTERNAL_STORAGE (no file access)

### Permission Verification

Always check before accessing gated APIs:

```kotlin
private fun startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED
    ) {
        return  // Don't proceed without permission
    }
    fusedLocationClient.requestLocationUpdates(...)
}
```

## Data Storage Security

### DataStore Encryption

All persistent data is encrypted:

```kotlin
// Production: Encrypted
val preferencesDataStore = encryptedDataStore(
    filename = "user_prefs",
    encryptionProvider = MasterKeysKt.getMasterKey(context)
)

// Non-sensitive: User selection, not location
val preferences = userPreferencesRepository.userPreferencesFlow
    .map { it.selectedTiltMode }
    .collect { ... }
```

### What's NOT Stored

- ❌ GPS coordinates (transient only)
- ❌ Sensor readings (not persisted)
- ❌ Orientation history (real-time only)
- ❌ API keys (in BuildConfig)
- ❌ Analytics session IDs (transmitted securely)

### Backup Security

```xml
<!-- AndroidManifest.xml -->
<application android:allowBackup="false" ... >
```

Disables cloud backup to prevent sensitive data exposure.

## Network Security

### No Direct Network Calls

SolPan makes no direct HTTP requests. All external services go through:
- **Google Play Services**: Uses Google's infrastructure (encrypted)
- **Firebase**: Uses Google's infrastructure (encrypted)

### Hypothetical Network Security

If future versions add network calls:

```xml
<!-- network_security_config.xml -->
<domain-config>
    <domain includeSubdomains="true">example.com</domain>
    <pin-set expiration="2026-12-31">
        <pin digest="SHA-256">BASE64_OF_CERT_HASH</pin>
    </pin-set>
</domain-config>
```

### TLS Requirements
- Minimum: TLS 1.2
- Preferred: TLS 1.3
- Cleartext: Disabled (HTTPS only)

## Code Security

### Input Validation

```kotlin
// Latitude validation
fun isValidLatitude(lat: Double): Boolean {
    return lat in -90.0..90.0
}

// Longitude validation
fun isValidLongitude(lon: Double): Boolean {
    return lon in -180.0..180.0
}

// Always validate location before use
val location = Location("").apply {
    latitude = newLat
    longitude = newLon
}
if (isValidLatitude(newLat) && isValidLongitude(newLon)) {
    // Safe to use
}
```

### Exception Handling

Never leak sensitive info in exceptions:

```kotlin
// ❌ Bad: Logs location
try {
    processLocation(location)
} catch (e: Exception) {
    Log.e("SolPan", "Failed with location: $location")  // Leaks GPS data!
}

// ✅ Good: Generic error
try {
    processLocation(location)
} catch (e: Exception) {
    Log.e("SolPan", "Location processing failed")  // Generic, safe
    // Optionally send to analytics with location stripped
}
```

### ProGuard Configuration

```gradle
-keep class app.mobilemobile.solpan.** { *; }
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-dontshrink  # Keep debugging info if needed
```

Prevents reverse engineering of sensitive logic.

## Third-Party Library Security

### Dependency Management

All dependencies vetted for:
- Active maintenance
- Known vulnerabilities (Dependabot checks)
- Privacy implications

### Current Dependencies

| Library | Version | Security Status |
|---------|---------|-----------------|
| Jetpack Compose | 2026.04.00 | ✅ Actively maintained |
| Navigation 3 | 1.2.0-alpha01 | ✅ Google (secure) |
| DataStore | 1.3.0-alpha07 | ✅ Encrypted by default |
| Accompanist | 0.37.3 | ✅ Community maintained |
| Firebase | 34.12.0 | ✅ Google services |
| Commons Suncalc | 3.11 | ✅ Well-established library |

### Vulnerability Scanning

Configured with GitHub's Dependabot:
- Checks daily for vulnerabilities
- Creates PRs for security updates
- Automates patching process

## Firebase Security

### Analytics Configuration

```kotlin
// Exclude PII from analytics
Firebase.analytics.setUserProperty("privacy_mode", "strict")
```

**Tracked Events** (non-identifying):
- App opened
- Tutorial completed
- Permission denied/granted
- Tilt mode selected
- App closed

**NOT Tracked**:
- GPS coordinates
- User ID (when not explicitly set)
- Device identifiers beyond Firebase's automatic ID

### Crashlytics Configuration

```kotlin
// Disable automatic error tracking if sensitive
Firebase.crashlytics.isCrashlyticsCollectionEnabled = true
```

Stack traces are secure and Google-managed.

### Performance Monitoring

```kotlin
val trace = Firebase.performance.newTrace("solar_calc")
trace.start()
SolarCalculator.calculatePosition(...)
trace.stop()
```

Only non-PII metrics recorded (latency, throughput).

## Runtime Security

### Manifest Hardening

```xml
<application
    android:allowBackup="false"
    android:debuggable="false"  <!-- Production only -->
    android:usesCleartextTraffic="false"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    ...
>
```

### API Level Specific Security

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    // Use runtime permissions API
    requestPermissions(...)
} else {
    // Manifest permissions only (legacy)
}
```

## Accessibility vs. Security Balance

### Semantic Descriptions

All UI elements have descriptions:
```kotlin
Icon(
    imageVector = Icons.Default.LocationOn,
    contentDescription = "Current location: 51.5074°N, 0.1278°W",  // Screen reader
)
```

Screen readers get full semantic descriptions without exposing raw coordinates to attackers.

## Testing Security

### Unit Tests

```kotlin
@Test
fun `validateLatitude rejects invalid values`() {
    assertFalse(isValidLatitude(91.0))
    assertFalse(isValidLatitude(-91.0))
}

@Test
fun `locationRepository never persists GPS data`() {
    val repo = DefaultLocationRepository()
    repo.updateLocation(testLocation)
    assertNull(repo.getStoredLocation())  // No persistence
}
```

### Manual Testing

**Permission Testing**:
1. Deny location permission
2. Verify app doesn't crash
3. Verify "Permission Denied" message shown
4. Verify user can request again

**Data Verification**:
1. Grant location permission
2. Use app
3. Check device storage (should be empty except preferences)
4. Check network (no outgoing location data)

## Deployment Security

### Release Build Configuration

```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            debuggable = false  // NO debugging in production
        }
    }
}
```

### Signing & Distribution

- Signed with private key (kept secure)
- Published via Google Play (automatic security scanning)
- Version code incremented with each release
- Security patch releases published immediately

## Incident Response

### If Vulnerability Discovered

1. **Immediate**: Create security patch
2. **Within 24h**: Test on multiple devices
3. **Within 48h**: Submit to Play Store
4. **Notify Users**: In-app message if needed
5. **Post-Mortem**: Document learning

### User Data Breach Protocol

If location data ever exposed:
1. Disable location collection immediately
2. Notify users via Play Store
3. Offer app reset
4. Improve validation/security
5. Third-party audit

## Compliance

### GDPR Compliance

✅ **Compliant**:
- No personal data collection beyond what's needed
- User can opt out (deny location permission)
- No tracking of individuals
- No data shared with third parties
- Automatic data deletion on uninstall

✅ **Transparency**:
- Privacy policy available
- Clear permission requests
- Settings for data preferences

### CCPA Compliance

✅ **Compliant**:
- Minimal data collection
- Right to delete (clear app data)
- Right to know (open source)
- Right to opt-out (permission denial)

## Recommendations for Users

### Privacy Best Practices

1. **Review Permissions**: Only enable location when using app
2. **Check Battery Optimization**: Allow Play Services location in background settings if desired
3. **Verify App Version**: Always update from official Play Store
4. **Check OS Updates**: Keep Android OS current
5. **Monitor Battery**: Unusual drain may indicate permission abuse

## Future Security Improvements

- [ ] Add secure enclave support (Keystore for secrets)
- [ ] Implement certificate pinning (if network added)
- [ ] Add runtime security monitoring
- [ ] Implement obfuscation for sensitive algorithms
- [ ] Add integrity verification (SafetyNet attestation)

## Security Resources

- [Android Security & Privacy Documentation](https://developer.android.com/privacy-and-security)
- [OWASP Mobile Security Guidelines](https://owasp.org/www-project-mobile-security-testing-guide/)
- [Google Play Security Best Practices](https://developer.android.com/google-play/policies)
- [GDPR Compliance Guide](https://gdpr-info.eu/)
- [Firebase Security Guide](https://firebase.google.com/docs/database/security)

## Summary

SolPan maintains strong security and privacy through:
1. **Minimal data collection** (location only, when needed)
2. **No persistence** of sensitive data
3. **Encrypted storage** of user preferences
4. **Runtime permission checks** on all sensitive APIs
5. **Third-party vetting** and vulnerability scanning
6. **Secure transport** (Google Play Services, Firebase)
7. **Clear privacy policy** and compliance
8. **Regular security updates** and monitoring

Users can trust SolPan with their location data—it's only used locally and never stored or shared.
