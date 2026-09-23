package app.mobilemobile.solpan.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Walks every tilt mode so the generated profile covers the solar maths, sensor fusion, location
 * updates, state management and the composables each mode renders.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule val rule = BaselineProfileRule()

    @Test
    fun generate() =
        rule.collect("app.mobilemobile.solpan") {
            pressHome()
            startActivityAndWait()
            dismissTutorialIfPresent()

            // The labels are the bottom bar tabs as the app renders them.
            listOf("Realtime", "Summer", "Winter", "Spring", "Year Round", "Realtime").forEach {
                selectTiltMode(it)
            }
        }
}

private const val UI_TIMEOUT_MS = 5_000L

/** The first run shows a tutorial overlay that covers the tab bar. */
private fun MacrobenchmarkScope.dismissTutorialIfPresent() {
    device.wait(Until.findObject(By.textContains("Got it")), UI_TIMEOUT_MS)?.click()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.selectTiltMode(label: String) {
    device.wait(Until.findObject(By.textContains(label)), UI_TIMEOUT_MS)?.click()
    device.waitForIdle()
}
