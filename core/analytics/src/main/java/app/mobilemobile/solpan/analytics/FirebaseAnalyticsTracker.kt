/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.analytics

// Dummy implementation for build verification without Firebase SDK
class FirebaseAnalyticsTracker : AnalyticsTracker {
    override fun logTutorialStarted() { }
    override fun logTutorialEnded() { }
    override fun logPermissionResult(granted: Boolean) { }
}
