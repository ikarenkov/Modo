package com.github.terrakok.modo.android.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.get

internal class ModoViewModel : ViewModel() {

    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    fun clear(key: String) {
        val viewModelStore = viewModelStores.remove(key)
        viewModelStore?.clear()
    }

    override fun onCleared() {
        viewModelStores.values.forEach { it.clear() }
        viewModelStores.clear()
    }

    fun getViewModelStore(key: String): ViewModelStore {
        var viewModelStore = viewModelStores[key]
        if (viewModelStore == null) {
            viewModelStore = ViewModelStore()
            viewModelStores[key] = viewModelStore
        }
        return viewModelStore
    }

    override fun toString(): String {
        val sb = StringBuilder("ViewModelStores (")
        val viewModelStoreIterator: Iterator<String> = viewModelStores.keys.iterator()
        while (viewModelStoreIterator.hasNext()) {
            sb.append(viewModelStoreIterator.next())
            if (viewModelStoreIterator.hasNext()) {
                sb.append(", ")
            }
        }
        sb.append(')')
        return sb.toString()
    }

    companion object {
        private val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>) = ModoViewModel() as T
        }

        fun getInstance(viewModelStore: ViewModelStore): ModoViewModel =
            ViewModelProvider(viewModelStore, factory).get()
    }
}
