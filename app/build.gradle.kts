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

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.perf)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.spotless)
}

android {
    namespace = "app.mobilemobile.solpan"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.mobilemobile.solpan"
        minSdk = 26
        targetSdk = 36
        versionCode = (findProperty("appVersionCode") as String? ?: "1").toInt()
        versionName = findProperty("appVersionName") as String? ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFileFromEnv = System.getenv("ANDROID_KEYSTORE_PATH")
            val keyAliasFromEnv = System.getenv("ANDROID_KEY_ALIAS")
            val storePasswordFromEnv = System.getenv("ANDROID_STORE_PASSWORD")
            val keyPasswordFromEnv = System.getenv("ANDROID_KEY_PASSWORD")

            if (keystoreFileFromEnv != null && file(keystoreFileFromEnv).exists()) {
                storeFile = file(keystoreFileFromEnv)
                this.keyAlias = keyAliasFromEnv
                this.storePassword = storePasswordFromEnv
                this.keyPassword = keyPasswordFromEnv
            }
        }
    }

    @Suppress("UnstableApiUsage")
    androidResources { generateLocaleConfig = true }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin { jvmToolchain(21) }

dependencies {
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.test)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.bundles.androidxLifecycle)
    implementation(libs.bundles.androidxMaterial)
    implementation(libs.bundles.androidxNavigation)
    implementation(libs.bundles.androidxUi)
    implementation(libs.bundles.firebase)
    implementation(libs.commons.suncalc)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.play.services.location)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))
    testImplementation(libs.junit)
}

// spotless { // if you are using build.gradle.kts, instead of 'spotless {' use:
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("src/*/kotlin/**/*.kt", "src/*/java/**/*.kt")
        ktfmt()
        ktlint("1.7.1").setEditorConfigPath("../.editorconfig")
        licenseHeaderFile(rootProject.file("copyright.kt"))
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktfmt()
        ktlint("1.7.1").setEditorConfigPath("../.editorconfig")
    }
}
