package com.github.terrakok.modo

import androidx.compose.ui.window.DialogProperties

@ExperimentalModoApi
interface DialogScreen : Screen {

    /**
     * When true, then we don't hide dialog when a new dialog appears on the top.
     */
    val permanentDialog: Boolean get() = false

    fun provideDialogConfig(): DialogConfig = DialogConfig.System()

    sealed interface DialogConfig {

        data class System(
            val useSystemDim: Boolean = true,
            val dialogProperties: DialogProperties = DialogProperties()
        ) : DialogConfig

        data object Custom : DialogConfig

    }

}