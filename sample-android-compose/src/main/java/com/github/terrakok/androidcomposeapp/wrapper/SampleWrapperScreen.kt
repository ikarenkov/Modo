package com.github.terrakok.androidcomposeapp.wrapper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.CustomModoReducer
import com.github.terrakok.androidcomposeapp.SampleScreen
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.SetState
import com.github.terrakok.modo.android.compose.NavigationStateParceler
import com.github.terrakok.modo.android.compose.WrapperComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<NavigationState, NavigationStateParceler>
class SampleWrapperScreen private constructor(
    override var saveableWreppeeState: NavigationState = NavigationState(listOf(SampleScreen(1))),
    override val screenKey: String = uniqueScreenKey
) : WrapperComposeScreen("SampleWrapperScreen", saveableWreppeeState, MultiReducer(CustomModoReducer())) {

    @IgnoredOnParcel
    var outerModo: Modo? = null

    constructor(
        outerModo: Modo
    ) : this() {
        this.outerModo = outerModo
    }

    override fun onNewState(state: NavigationState) {
        outerModo?.let {
            it.dispatch(SetState(it.state))
        }
    }

    @Composable
    override fun Content(innerContent: @Composable () -> Unit) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Cyan)
                .padding(30.dp)
        ) {
            innerContent()
        }
    }
}