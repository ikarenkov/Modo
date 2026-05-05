package com.github.terrakok.modo.sample.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppSetting<T> internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val defaultValue: T,
    scope: CoroutineScope
) {
    val stateFlow: StateFlow<T> = dataStore.data
        .map { it[key] ?: defaultValue }
        .stateIn(scope, SharingStarted.Eagerly, defaultValue)

    val value: T get() = stateFlow.value

    suspend fun update(value: T) {
        dataStore.edit { it[key] = value }
    }

    class Factory(
        private val dataStore: DataStore<Preferences>,
        private val scope: CoroutineScope
    ) {
        fun boolean(key: String, default: Boolean) = AppSetting(dataStore, booleanPreferencesKey(key), default, scope)
        fun int(key: String, default: Int) = AppSetting(dataStore, intPreferencesKey(key), default, scope)
        fun string(key: String, default: String) = AppSetting(dataStore, stringPreferencesKey(key), default, scope)
        fun float(key: String, default: Float) = AppSetting(dataStore, floatPreferencesKey(key), default, scope)
        fun long(key: String, default: Long) = AppSetting(dataStore, longPreferencesKey(key), default, scope)
        fun stringSet(key: String, default: Set<String>) = AppSetting(dataStore, stringSetPreferencesKey(key), default, scope)
    }
}
