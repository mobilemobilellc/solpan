/*
 * Copyright 2025 MobileMobile LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package app.mobilemobile.solpan.security

/**
 * Security best practices for SolPan.
 *
 * Following OWASP Mobile Security Guidelines and Android Security & Privacy documentation.
 */

/**
 * Sensitive data handling guidelines.
 *
 * Rules:
 * - Location data: Only stored in DataStore with encryption, transmitted only to Google Play Services
 * - Sensor data: Processed locally, never persisted
 * - User preferences: Stored encrypted in DataStore
 * - API keys: Stored in BuildConfig or secure storage (not in code)
 * - Analytics: Configured to exclude PII
 */
object SensitiveDataPolicy {
    // Location data is only collected when user grants permission
    const val LOCATION_MIN_TIME_MS = 10_000L // Minimum time between updates
    const val LOCATION_MIN_DISTANCE_M = 10f  // Minimum distance for update

    // Sensor data is transient and not logged
    const val SENSOR_DATA_RETENTION_MS = 0L   // No retention

    // Clear sensitive data on app background
    const val CLEAR_ON_PAUSE = true
}

/**
 * Permission security model.
 *
 * SolPan requests only necessary permissions:
 * - ACCESS_FINE_LOCATION: For accurate GPS location
 * - ACCESS_COARSE_LOCATION: Fallback for network-based location
 * - VIBRATE: For haptic feedback (non-sensitive)
 *
 * All location access is with user's explicit runtime permission.
 */
object PermissionSecurityModel {
    val REQUIRED_PERMISSIONS = listOf(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
    )

    val OPTIONAL_PERMISSIONS = listOf(
        "android.permission.VIBRATE",
    )
}

/**
 * Network security configuration.
 *
 * Although SolPan doesn't make direct network calls (uses Google Play Services),
 * we follow best practices:
 * - Certificate pinning for any third-party APIs
 * - HTTPS only
 * - TLS 1.2+ minimum
 */
object NetworkSecurityPolicy {
    // See network_security_config.xml for cert pinning configuration
    const val MIN_TLS_VERSION = "1.2"
    const val CLEARTEXT_ALLOWED = false // HTTPS only
}

/**
 * Data export security.
 *
 * - Backups: Can be disabled for sensitive data via allowBackup=false
 * - Encrypted: All data at rest is encrypted via DataStore
 * - No debug database files in production
 */
object DataExportSecurity {
    const val ALLOW_BACKUP = false // Disable backup for production
    const val ENCRYPTION_ENABLED = true // DataStore encryption
}

/**
 * Vulnerability scanning and updates.
 *
 * - Dependencies checked via Dependabot (GitHub)
 * - Build-time security scanning
 * - Regular security audits of critical code paths
 * - Stay current with platform updates
 */
object VulnerabilityManagement {
    // Metadata for CI/CD scanning
    const val SCAN_FREQUENCY = "weekly"
    const val AUTO_UPDATE_MINORS = true
    const val AUTO_UPDATE_PATCHES = true
}
