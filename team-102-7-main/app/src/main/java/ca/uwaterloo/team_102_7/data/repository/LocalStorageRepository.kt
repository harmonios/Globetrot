package ca.uwaterloo.team_102_7.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

// Extension property to create DataStore
val Context.dataStore by preferencesDataStore("local_storage_preferences")

class LocalStorageRepository(private val context: Context) {

    private val userIDKey = intPreferencesKey("user_id_key")

    private fun getUserCheckboxKey(userId: Int): Preferences.Key<Set<String>> {
        return stringSetPreferencesKey("checkbox_items_key_$userId")
    }

    suspend fun saveCheckboxItems(userId: Int?, items: List<String>) {
        if (userId == null) return
        val key = getUserCheckboxKey(userId)
        context.dataStore.edit { preferences ->
            preferences[key] = items.toSet() // Save as a Set<String>
        }
    }

    fun getCheckboxItemsFlow(userId: Int?): Flow<List<String>> {
        if (userId == null) return flowOf(listOf())
        val key = getUserCheckboxKey(userId)
        return context.dataStore.data.map { preferences ->
            preferences[key]?.toList() ?: emptyList() // Default to an empty list if nothing is saved
        }
    }

    suspend fun saveUserUUID(userID: Int) {
        context.dataStore.edit { preferences ->
            preferences[userIDKey] = userID
        }
    }

    fun getUserIDFlow(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[userIDKey] ?: 0
        }
    }
}
