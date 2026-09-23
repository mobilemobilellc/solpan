/*
 * Copyright 2025 MobileMobile LLC
 */
plugins {
    id("solpan.android.library")
}

android {
    namespace = "app.mobilemobile.solpan.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.datastore.preferences)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
