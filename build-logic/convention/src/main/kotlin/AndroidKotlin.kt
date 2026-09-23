/*
 * Copyright 2025 MobileMobile LLC
 */

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(extension: CommonExtension) {
    extension.apply {
        compileSdk = 37
        compileSdkMinor = 1

        defaultConfig.apply {
            minSdk = 26
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        // Without this a library module emits no .exec, so its tests count for nothing in the
        // aggregate coverage report.
        buildTypes.getByName("debug").enableUnitTestCoverage = true

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    configure<KotlinAndroidProjectExtension> { jvmToolchain(21) }
}
