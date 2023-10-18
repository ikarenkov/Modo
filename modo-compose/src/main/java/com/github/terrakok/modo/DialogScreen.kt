package com.github.terrakok.modo

import androidx.compose.ui.window.DialogProperties

@ExperimentalModoApi
interface DialogScreen : Screen {

    fun provideDialogConfig(): DialogConfig = DialogConfig.System()

    sealed interface DialogConfig {

        data class System(
            val useSystemDim: Boolean = true,
            val dialogProperties: DialogProperties = DialogProperties()
        ) : DialogConfig

        data class Custom(
            val stackMode: Boolean = true,
        ) : DialogConfig

    }

}