package com.github.terrakok.modo.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.terrakok.modo.sample.settings.AppSetting
import kotlinx.coroutines.CoroutineScope

class SampleAppSettings private constructor(
    dataStore: DataStore<Preferences>,
    scope: CoroutineScope
) {
    private val factory = AppSetting.Factory(dataStore, scope)

    val showNavigationTree = factory.boolean("show_navigation_tree", default = true)
    val navTreeVisibleScreens = factory.int("nav_tree_visible_screens", default = 2)

    companion object {
        lateinit var instance: SampleAppSettings
            private set

        internal fun init(dataStore: DataStore<Preferences>, scope: CoroutineScope) {
            instance = SampleAppSettings(dataStore, scope)
        }
    }
}
