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
package app.mobilemobile.solpan.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.data.TiltMode
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/**
 * Formats the Double to a string with a specified number of decimal digits, using a period (.) as
 * the decimal separator. Example: 3.14159.format(2) -> "3.14"
 */
fun Double.format(digits: Int): String = "%.${digits}f".format(Locale.US, this)

/**
 * Formats the Float to a string with a specified number of decimal digits, using a period (.) as
 * the decimal separator. Example: 3.14159f.format(2) -> "3.14"
 */
fun Float.format(digits: Int): String = "%.${digits}f".format(Locale.US, this)

/**
 * Rounds the Float to a specified number of decimal places. Uses RoundingMode.HALF_UP.
 *
 * @param decimalPlaces The number of decimal places to round to.
 * @return The rounded Float.
 */
fun Float.roundTo(decimalPlaces: Int): Float {
    require(decimalPlaces >= 0) { "Decimal places cannot be negative" }
    return BigDecimal(this.toString()).setScale(decimalPlaces, RoundingMode.HALF_UP).toFloat()
}

// Extension function for a more readable display name for TiltMode
@Composable
fun TiltMode.displayName(): String =
    when (this) {
        TiltMode.YEAR_ROUND -> stringResource(id = R.string.tilt_mode_year_round)
        TiltMode.SUMMER -> stringResource(id = R.string.tilt_mode_summer)
        TiltMode.WINTER -> stringResource(id = R.string.tilt_mode_winter)
        TiltMode.SPRING_AUTUMN -> stringResource(id = R.string.tilt_mode_spring_autumn)
        TiltMode.REALTIME -> stringResource(id = R.string.tilt_mode_realtime)
    }
