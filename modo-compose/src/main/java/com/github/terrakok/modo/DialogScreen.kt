package com.github.terrakok.modo

import androidx.compose.ui.window.DialogProperties

@ExperimentalModoApi
interface DialogScreen : Screen {

    fun provideDialogConfig(): DialogConfig = DialogConfig()

    data class DialogConfig(
        val useSystemDim: Boolean = true,
        val dialogProperties: DialogProperties = DialogProperties()
    )

}