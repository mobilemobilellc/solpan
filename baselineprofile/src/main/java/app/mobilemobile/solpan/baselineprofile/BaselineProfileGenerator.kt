package app.mobilemobile.solpan.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect("app.mobilemobile.solpan") {
        pressHome()
        startActivityAndWait()

        // Navigate through all major tilt modes to warm up Compose rendering and business logic
        // This ensures the compiled baseline profile covers:
        // - Solar calculations (SolarCalculator)
        // - Sensor fusion (DeviceOrientationController)
        // - Location updates (DeviceLocationManager)
        // - State management (SolPanViewModel)
        // - UI composables (all SolPanScreen variants)

        device.waitForIdle()

        // REALTIME mode - current sun position tracking
        clickTab("Realtime")
        device.waitForIdle()

        // SUMMER mode - summer solstice optimization
        clickTab("Summer")
        device.waitForIdle()

        // WINTER mode - winter solstice optimization
        clickTab("Winter")
        device.waitForIdle()

        // SPRING_AUTUMN mode - equinox optimization
        clickTab("Spring Autumn")
        device.waitForIdle()

        // YEAR_ROUND mode - averaged annual optimization
        clickTab("Year Round")
        device.waitForIdle()

        // Return to REALTIME
        clickTab("Realtime")
        device.waitForIdle()
    }

    private fun clickTab(contentDescription: String) {
        device.findObject(UiSelector().descriptionContains(contentDescription)).clickAndWaitForNewWindow()
    }
}
