package com.github.terrakok.modo.model

import com.github.terrakok.modo.Screen

fun Screen.dependenciesSortedByRemovePriority(): Sequence<Any> =
    ScreenModelStore.screenDependenciesSortedByRemovePriority(this)