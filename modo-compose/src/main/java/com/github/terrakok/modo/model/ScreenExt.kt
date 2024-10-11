package com.github.terrakok.modo.model

import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.lifecycle.LifecycleDependency

fun Screen.dependenciesSortedByRemovePriority(): Sequence<Any> =
    ScreenModelStore.screenDependenciesSortedByRemovePriority(this)

fun Screen.lifecycleDependency(): LifecycleDependency? =
    ScreenModelStore.getDependencyOrNull(this, LifecycleDependency.KEY)