/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "solpan_settings")

object UserPreferencesKeys {
    val TUTORIAL_SEEN = booleanPreferencesKey("tutorialSeen")
}

interface UserPreferencesRepository {
    val tutorialSeen: Flow<Boolean>
    suspend fun setTutorialSeen(seen: Boolean)
}

class DataStoreUserPreferencesRepository(
    private val context: Context,
) : UserPreferencesRepository {
    override val tutorialSeen: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[UserPreferencesKeys.TUTORIAL_SEEN] ?: false }

    override suspend fun setTutorialSeen(seen: Boolean) {
        context.dataStore.edit { prefs -> prefs[UserPreferencesKeys.TUTORIAL_SEEN] = seen }
    }
}
