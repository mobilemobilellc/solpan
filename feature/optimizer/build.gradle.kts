/*
 * Copyright 2025 MobileMobile LLC
 */
plugins {
    id("solpan.android.library")
}

android {
    namespace = "app.mobilemobile.solpan.feature.optimizer"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:solar"))
    implementation(project(":core:data"))
    implementation(project(":core:analytics"))
    implementation(project(":core:designsystem"))
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
