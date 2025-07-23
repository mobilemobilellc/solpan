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
package app.mobilemobile.solpan.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val SolPanDarkColorScheme =
    darkColorScheme(
        primary = SolPanPrimaryDark,
        onPrimary = SolPanOnPrimaryDark,
        primaryContainer = SolPanPrimaryContainerDark,
        onPrimaryContainer = SolPanOnPrimaryContainerDark,
        secondary = SolPanSecondaryDark,
        onSecondary = SolPanOnSecondaryDark,
        secondaryContainer = SolPanSecondaryContainerDark,
        onSecondaryContainer = SolPanOnSecondaryContainerDark,
        tertiary = SolPanTertiaryDark,
        onTertiary = SolPanOnTertiaryDark,
        tertiaryContainer = SolPanTertiaryContainerDark,
        onTertiaryContainer = SolPanOnTertiaryContainerDark,
        error = SolPanErrorDark,
        onError = SolPanOnErrorDark,
        errorContainer = SolPanErrorContainerDark,
        onErrorContainer = SolPanOnErrorContainerDark,
        background = SolPanBackgroundDark,
        onBackground = SolPanOnBackgroundDark,
        surface = SolPanSurfaceDark,
        onSurface = SolPanOnSurfaceDark,
        surfaceVariant = SolPanSurfaceVariantDark,
        onSurfaceVariant = SolPanOnSurfaceVariantDark,
        outline = SolPanOutlineDark,
    )

private val SolPanLightColorScheme =
    lightColorScheme(
        primary = SolPanPrimaryLight,
        onPrimary = SolPanOnPrimaryLight,
        primaryContainer = SolPanPrimaryContainerLight,
        onPrimaryContainer = SolPanOnPrimaryContainerLight,
        secondary = SolPanSecondaryLight,
        onSecondary = SolPanOnSecondaryLight,
        secondaryContainer = SolPanSecondaryContainerLight,
        onSecondaryContainer = SolPanOnSecondaryContainerLight,
        tertiary = SolPanTertiaryLight,
        onTertiary = SolPanOnTertiaryLight,
        tertiaryContainer = SolPanTertiaryContainerLight,
        onTertiaryContainer = SolPanOnTertiaryContainerLight,
        error = SolPanErrorLight,
        onError = SolPanOnErrorLight,
        errorContainer = SolPanErrorContainerLight,
        onErrorContainer = SolPanOnErrorContainerLight,
        background = SolPanBackgroundLight,
        onBackground = SolPanOnBackgroundLight,
        surface = SolPanSurfaceLight,
        onSurface = SolPanOnSurfaceLight,
        surfaceVariant = SolPanSurfaceVariantLight,
        onSurfaceVariant = SolPanOnSurfaceVariantLight,
        outline = SolPanOutlineLight,
    )

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SolPanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Keep dynamic color option
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                SolPanDarkColorScheme
            }

            // Use SolPan dark scheme
            else -> {
                SolPanLightColorScheme
            } // Use SolPan light scheme
        }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography, // Defined in Type.kt
        content = content,
    )
}
