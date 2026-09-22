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
package app.mobilemobile.solpan.analytics

interface AnalyticsTracker {
    fun logTutorialStarted()

    fun logTutorialEnded()

    fun logPermissionResult(granted: Boolean)
}

/** Idiomatic Kotlin 2026: Using context parameters for analytics. */
context(tracker: AnalyticsTracker)
fun logTutorialStarted() = tracker.logTutorialStarted()

context(tracker: AnalyticsTracker)
fun logTutorialEnded() = tracker.logTutorialEnded()

context(tracker: AnalyticsTracker)
fun logPermissionResult(granted: Boolean) = tracker.logPermissionResult(granted)
