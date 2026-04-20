plugins {
    alias(libs.plugins.android.test)
}

android {
    namespace = "app.mobilemobile.solpan.baselineprofile"
    compileSdk = 37
    defaultConfig {
        minSdk = 28
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    targetProjectPath = ":app"
}

dependencies {
    implementation(libs.uiautomator)
    implementation(libs.benchmark.macro.junit4)
}

androidComponents {
    onVariants(selector().all()) { v ->
        v.instrumentationRunnerArguments.run {
            put("targetAppId", "app.mobilemobile.solpan")
        }
    }
}
