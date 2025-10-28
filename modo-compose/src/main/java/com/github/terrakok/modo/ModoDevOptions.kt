package com.github.terrakok.modo

import android.util.Log
import com.github.terrakok.modo.ModoDevOptions.ValidationFailedStrategy

object ModoDevOptions {

    var onIllegalScreenModelStoreAccess: ValidationFailedStrategy = ValidationFailedStrategy { throwable ->
        Log.e("Modo", "Modo internal error", throwable)
    }

    var onIllegalClearState: ValidationFailedStrategy = ValidationFailedStrategy { throwable ->
        Log.e("Modo", "Modo internal error", throwable)
    }

    var onScreenDisposeListener: ((Screen) -> Unit)? = null
    var onScreenPreDisposeListener: ((Screen) -> Unit)? = null

    internal const val REPORT_ISSUE_URL = "You can report issue here https://github.com/terrakok/Modo/issues"

    fun interface ValidationFailedStrategy {
        fun validationFailed(throwable: Throwable)
    }

}