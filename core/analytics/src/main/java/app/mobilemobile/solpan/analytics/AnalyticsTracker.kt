/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.analytics

interface AnalyticsTracker {
    fun logTutorialStarted()
    fun logTutorialEnded()
    fun logPermissionResult(granted: Boolean)
}

/**
 * Idiomatic Kotlin 2026: Using context parameters for analytics.
 */
context(tracker: AnalyticsTracker)
fun logTutorialStarted() = tracker.logTutorialStarted()

context(tracker: AnalyticsTracker)
fun logTutorialEnded() = tracker.logTutorialEnded()

context(tracker: AnalyticsTracker)
fun logPermissionResult(granted: Boolean) = tracker.logPermissionResult(granted)
