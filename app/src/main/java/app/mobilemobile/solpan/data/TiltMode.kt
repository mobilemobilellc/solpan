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
package app.mobilemobile.solpan.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

enum class TiltMode(@param:StringRes val titleRes: Int, val icon: ImageVector) {
  YEAR_ROUND(
    titleRes = app.mobilemobile.solpan.R.string.tilt_mode_year_round,
    icon = Icons.Filled.Cached,
  ),
  SUMMER(titleRes = app.mobilemobile.solpan.R.string.tilt_mode_summer, icon = Icons.Filled.WbSunny),
  WINTER(titleRes = app.mobilemobile.solpan.R.string.tilt_mode_winter, icon = Icons.Filled.AcUnit),
  SPRING_AUTUMN(
    titleRes = app.mobilemobile.solpan.R.string.tilt_mode_spring_autumn,
    icon = Icons.Filled.Eco,
  ),
  REALTIME(
    titleRes = app.mobilemobile.solpan.R.string.tilt_mode_realtime,
    icon = Icons.Filled.AccessTime,
  ),
}
