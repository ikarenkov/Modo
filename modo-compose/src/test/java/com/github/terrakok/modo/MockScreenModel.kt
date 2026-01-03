package com.github.terrakok.modo

import com.github.terrakok.modo.model.ScreenModel

class MockScreenModel(val id: String = "", val onDispose: () -> Unit = {}) : ScreenModel {
    override fun onDispose() {
        onDispose.invoke()
    }
}