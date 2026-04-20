/*
 * Copyright 2025 MobileMobile LLC
 */
plugins {
    id("solpan.android.library")
}

android {
    namespace = "app.mobilemobile.solpan.core.solar"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.commons.suncalc)
}
