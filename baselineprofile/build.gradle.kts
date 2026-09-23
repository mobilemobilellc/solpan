plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
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

baselineProfile {
    // Generation needs a real device; there is no managed device configured.
    useConnectedDevices = true
}

dependencies {
    implementation(libs.junit)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.test.runner)
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
