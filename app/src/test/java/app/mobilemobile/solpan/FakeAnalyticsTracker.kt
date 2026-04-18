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
package app.mobilemobile.solpan

import app.mobilemobile.solpan.analytics.AnalyticsTracker

/** No-op implementation of AnalyticsTracker for unit tests. */
class FakeAnalyticsTracker : AnalyticsTracker {
    override fun logTutorialStarted() = Unit

    override fun logTutorialEnded() = Unit

    override fun logPermissionResult(granted: Boolean) = Unit
}
