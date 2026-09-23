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
    id("solpan.android.application")
    id("solpan.jacoco.report")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.screenshot)
    alias(libs.plugins.dokka)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "app.mobilemobile.solpan"

    defaultConfig { applicationId = "app.mobilemobile.solpan" }

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
            } else if (System.getenv("CI") == null) {
                // Off CI, fall back to the debug key so release-derived variants can be built
                // locally. androidx.baselineprofile needs nonMinifiedRelease to be signable, and
                // that variant inherits this config. On CI the fallback is skipped, so a missing
                // keystore secret still fails loudly rather than shipping a debug-signed build.
                val debugConfig = signingConfigs.getByName("debug")
                storeFile = debugConfig.storeFile
                this.keyAlias = debugConfig.keyAlias
                this.storePassword = debugConfig.storePassword
                this.keyPassword = debugConfig.keyPassword
            }
        }
    }

    androidResources {
        generateLocaleConfig = true
        localeFilters.addAll(
            listOf(
                "ar",
                "b+es+419",
                "cs",
                "da",
                "de",
                "en",
                "es",
                "fi",
                "fr",
                "hi",
                "hu",
                "id",
                "it",
                "iw",
                "ja",
                "ko",
                "nl",
                "no",
                "pl",
                "pt",
                "ro",
                "ru",
                "sk",
                "sv",
                "th",
                "tr",
                "uk",
                "vi",
                "zh-rCN",
            ),
        )
    }

    buildTypes {
        release { signingConfig = signingConfigs.getByName("release") }
        debug { enableUnitTestCoverage = true }
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    implementation(project(":feature:optimizer"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:analytics"))
    implementation(project(":core:solar"))
    implementation(project(":core:designsystem"))

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
    implementation(libs.play.services.location)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.profileinstaller)
    implementation(platform(libs.androidx.compose.bom))
    baselineProfile(project(":baselineprofile"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    screenshotTestImplementation(platform(libs.androidx.compose.bom))
    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.ui.tooling)
}
