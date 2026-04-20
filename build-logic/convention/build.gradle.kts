/*
 * Copyright 2025 MobileMobile LLC
 */

plugins {
    `kotlin-dsl`
}

group = "app.mobilemobile.solpan.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "solpan.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "solpan.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("jacocoReport") {
            id = "solpan.jacoco.report"
            implementationClass = "JacocoReportConventionPlugin"
        }
    }
}
