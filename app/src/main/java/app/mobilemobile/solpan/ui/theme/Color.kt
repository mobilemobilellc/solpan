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

import androidx.compose.ui.graphics.Color

// SolPan Theme Colors - Extracted & Derived
val SolPanSkyBlue = Color(0xFF42A5F5) // Launcher background, good for light primary
val SolPanSunYellow = Color(0xFFFFD54F) // Launcher sun body
val SolPanSunRayYellow = Color(0xFFFFC107) // Launcher sun rays, good for accents/secondary

// Light Theme Semantic Colors (example derivations)
val SolPanPrimaryLight = SolPanSkyBlue // Sky blue as primary for light theme
val SolPanOnPrimaryLight = Color.White // Text on primary
val SolPanPrimaryContainerLight = Color(0xFFD0E6FF) // Lighter shade of sky blue
val SolPanOnPrimaryContainerLight = Color(0xFF001E30)

val SolPanSecondaryLight = SolPanSunRayYellow // Vibrant sun ray yellow as secondary
val SolPanOnSecondaryLight = Color.Black // Text on secondary
val SolPanSecondaryContainerLight = Color(0xFFFFEAB9)
val SolPanOnSecondaryContainerLight = Color(0xFF271900)

val SolPanTertiaryLight = SolPanSunYellow // Softer sun yellow as tertiary
val SolPanOnTertiaryLight = Color.Black // Text on tertiary
val SolPanTertiaryContainerLight = Color(0xFFFFFAE4)
val SolPanOnTertiaryContainerLight = Color(0xFF241A00)

val SolPanErrorLight = Color(0xFFB00020)
val SolPanOnErrorLight = Color.White
val SolPanErrorContainerLight = Color(0xFFFCD8DF)
val SolPanOnErrorContainerLight = Color(0xFF3E000A)

val SolPanBackgroundLight = Color(0xFFFDFBFF) // Very light, almost white background
val SolPanOnBackgroundLight = Color(0xFF1A1C1E) // Dark text on light background
val SolPanSurfaceLight = Color(0xFFFDFBFF) // Surface same as background for a flatter look
val SolPanOnSurfaceLight = Color(0xFF1A1C1E)
val SolPanSurfaceVariantLight = Color(0xFFDEE3EB) // For card backgrounds, etc.
val SolPanOnSurfaceVariantLight = Color(0xFF42474E)
val SolPanOutlineLight = Color(0xFF72777F)

// Dark Theme Semantic Colors (example derivations)
val SolPanPrimaryDark = Color(0xFF9ACBFA) // Lighter blue for dark theme primary (accessibility)
val SolPanOnPrimaryDark = Color(0xFF003355)
val SolPanPrimaryContainerDark = Color(0xFF004A78) // Original SkyBlue is too light, pick darker one
val SolPanOnPrimaryContainerDark = Color(0xFFCFE5FF)

val SolPanSecondaryDark = SolPanSunRayYellow // Sun ray yellow can work on dark too
val SolPanOnSecondaryDark = Color(0xFF3F2D00)
val SolPanSecondaryContainerDark = Color(0xFF5B4200)
val SolPanOnSecondaryContainerDark = Color(0xFFFFEAB9)

val SolPanTertiaryDark = SolPanSunYellow // Softer sun yellow for dark
val SolPanOnTertiaryDark = Color(0xFF3E2E00)
val SolPanTertiaryContainerDark = Color(0xFF5A4300)
val SolPanOnTertiaryContainerDark = Color(0xFFFFFAE4)

val SolPanErrorDark = Color(0xFFFFB4AB)
val SolPanOnErrorDark = Color(0xFF690005)
val SolPanErrorContainerDark = Color(0xFF93000A)
val SolPanOnErrorContainerDark = Color(0xFFFFDAD6)

val SolPanBackgroundDark = Color(0xFF1A1C1E) // Standard dark background
val SolPanOnBackgroundDark = Color(0xFFE2E2E5) // Light text on dark background
val SolPanSurfaceDark = Color(0xFF1A1C1E) // Surface same as background
val SolPanOnSurfaceDark = Color(0xFFE2E2E5)
val SolPanSurfaceVariantDark = Color(0xFF42474E) // For cards in dark theme
val SolPanOnSurfaceVariantDark = Color(0xFFC2C7CF)
val SolPanOutlineDark = Color(0xFF8C9199)
