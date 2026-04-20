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
package app.mobilemobile.solpan.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.model.TiltMode

val TiltMode.icon: ImageVector
    get() =
        when (this) {
            TiltMode.YEAR_ROUND -> Icons.Filled.Cached
            TiltMode.SUMMER -> Icons.Filled.WbSunny
            TiltMode.WINTER -> Icons.Filled.AcUnit
            TiltMode.SPRING_AUTUMN -> Icons.Filled.Eco
            TiltMode.REALTIME -> Icons.Filled.AccessTime
        }

val TiltMode.titleRes: Int
    get() =
        when (this) {
            TiltMode.YEAR_ROUND -> R.string.tilt_mode_year_round
            TiltMode.SUMMER -> R.string.tilt_mode_summer
            TiltMode.WINTER -> R.string.tilt_mode_winter
            TiltMode.SPRING_AUTUMN -> R.string.tilt_mode_spring_autumn
            TiltMode.REALTIME -> R.string.tilt_mode_realtime
        }
