/*
 * Copyright 2025 MobileMobile LLC
 */
plugins {
    id("solpan.android.library")
}

android {
    namespace = "app.mobilemobile.solpan.core.model"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
}
