/*
 * Copyright 2025 MobileMobile LLC
 */
plugins {
    id("solpan.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "app.mobilemobile.solpan.designsystem"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidxUi)
    implementation(libs.bundles.androidxMaterial)
}
