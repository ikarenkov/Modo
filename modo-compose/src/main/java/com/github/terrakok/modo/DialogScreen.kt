package com.github.terrakok.modo

import androidx.compose.ui.window.DialogProperties

interface DialogScreen : Screen {

    fun provideDialogProperties(): DialogProperties = DialogProperties()

}