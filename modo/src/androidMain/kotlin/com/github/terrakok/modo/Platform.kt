package com.github.terrakok.modo

import android.app.Activity

fun Activity.ComposeRenderer(
    getTransitionType: (oldState: NavigationState?, newState: NavigationState?) -> ScreenTransitionType = ::defaultCalculateTransitionType,
    content: RendererContent = defaultRendererContent
) = ComposeRenderer(
    exitAction = { finish() },
    getTransitionType,
    content
)