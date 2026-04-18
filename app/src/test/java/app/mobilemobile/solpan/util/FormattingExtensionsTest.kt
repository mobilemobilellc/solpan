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
package app.mobilemobile.solpan.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class FormattingExtensionsTest {
    @Test
    fun `Double format with 2 digits`() {
        assertEquals("3.14", 3.14159.format(2))
    }

    @Test
    fun `Double format with 0 digits`() {
        assertEquals("3", 3.14.format(0))
    }

    @Test
    fun `Double format uses period separator not comma`() {
        val result = 1.5.format(1)
        assertTrue("Expected period separator, got: $result", result.contains('.'))
        assertFalse("Should not contain comma: $result", result.contains(','))
    }

    @Test
    fun `Float format with 1 digit`() {
        assertEquals("3.1", 3.14f.format(1))
    }

    @Test
    fun `Float format negative value`() {
        assertEquals("-3.14", (-3.14159f).format(2))
    }

    @Test
    fun `roundTo positive half rounds up`() {
        assertEquals(3.15f, 3.145f.roundTo(2), 0.001f)
    }

    @Test
    fun `roundTo negative half rounds away from zero (HALF_UP)`() {
        // BigDecimal HALF_UP: -3.145 rounds away from zero → -3.15
        assertEquals(-3.15f, (-3.145f).roundTo(2), 0.001f)
    }

    @Test
    fun `roundTo zero decimal places`() {
        assertEquals(4.0f, 3.5f.roundTo(0), 0.001f)
    }

    @Test
    fun `roundTo returns same value when already exact`() {
        assertEquals(3.14f, 3.14f.roundTo(2), 0.0001f)
    }

    @Test
    fun `roundTo negative decimalPlaces throws`() {
        assertThrows(IllegalArgumentException::class.java) { 3.14f.roundTo(-1) }
    }
}
