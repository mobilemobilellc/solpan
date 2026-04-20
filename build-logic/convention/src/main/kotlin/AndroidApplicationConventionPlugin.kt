/*
 * Copyright 2025 MobileMobile LLC
 */

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroidApp(this)
                defaultConfig {
                    targetSdk = 37
                    versionCode = (findProperty("appVersionCode") as String? ?: "1").toInt()
                    versionName = findProperty("appVersionName") as String? ?: "1.0"
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }
                }
                buildFeatures {
                    compose = true
                    buildConfig = true
                }
            }
        }
    }
}
