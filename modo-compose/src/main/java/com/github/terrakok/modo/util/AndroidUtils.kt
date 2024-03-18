package com.github.terrakok.modo.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper

internal tailrec fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

internal tailrec fun Context.getApplication(): Application? = when (this) {
    is Application -> this
    is ContextWrapper -> baseContext.getApplication()
    else -> null
}