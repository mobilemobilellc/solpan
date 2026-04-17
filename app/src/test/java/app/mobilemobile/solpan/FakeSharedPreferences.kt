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
package app.mobilemobile.solpan

import android.content.SharedPreferences

/** In-memory [SharedPreferences] implementation for unit tests. */
class FakeSharedPreferences : SharedPreferences {
    private val data = mutableMapOf<String, Any?>()
    private val listeners = mutableSetOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun getAll(): MutableMap<String, *> = data.toMutableMap()

    override fun getString(
        key: String?,
        defValue: String?,
    ) = data[key] as? String ?: defValue

    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?,
    ) = data[key] as? MutableSet<String> ?: defValues

    override fun getInt(
        key: String?,
        defValue: Int,
    ) = data[key] as? Int ?: defValue

    override fun getLong(
        key: String?,
        defValue: Long,
    ) = data[key] as? Long ?: defValue

    override fun getFloat(
        key: String?,
        defValue: Float,
    ) = data[key] as? Float ?: defValue

    override fun getBoolean(
        key: String?,
        defValue: Boolean,
    ) = data[key] as? Boolean ?: defValue

    override fun contains(key: String?) = data.containsKey(key)

    override fun edit(): SharedPreferences.Editor = FakeEditor()

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) =
        listener?.let { listeners.add(it) }.let {}

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) =
        listener?.let { listeners.remove(it) }.let {}

    private inner class FakeEditor : SharedPreferences.Editor {
        private val pending = mutableMapOf<String, Any?>()
        private var clear = false

        override fun putString(
            key: String?,
            value: String?,
        ) = apply { key?.let { pending[it] = value } }

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?,
        ) = apply { key?.let { pending[it] = values } }

        override fun putInt(
            key: String?,
            value: Int,
        ) = apply { key?.let { pending[it] = value } }

        override fun putLong(
            key: String?,
            value: Long,
        ) = apply { key?.let { pending[it] = value } }

        override fun putFloat(
            key: String?,
            value: Float,
        ) = apply { key?.let { pending[it] = value } }

        override fun putBoolean(
            key: String?,
            value: Boolean,
        ) = apply { key?.let { pending[it] = value } }

        override fun remove(key: String?) = apply { key?.let { pending.remove(it) } }

        override fun clear() = apply { clear = true }

        override fun commit(): Boolean {
            applyChanges()
            return true
        }

        override fun apply() {
            applyChanges()
        }

        private fun applyChanges() {
            if (clear) data.clear()
            data.putAll(pending)
        }
    }
}
