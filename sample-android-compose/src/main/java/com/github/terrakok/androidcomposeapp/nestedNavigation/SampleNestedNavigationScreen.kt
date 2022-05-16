package com.github.terrakok.androidcomposeapp.nestedNavigation

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
import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.NavigationStateParceler
import com.github.terrakok.modo.android.compose.NestedNavigationComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<NavigationState, NavigationStateParceler>
class SampleNestedNavigationScreen(
    override var saveableWreppeeState: NavigationState = NavigationState(listOf(SampleScreen(1))),
    override val screenKey: String = uniqueScreenKey
) : NestedNavigationComposeScreen("SampleWrapperScreen", saveableWreppeeState) {

    @IgnoredOnParcel
    override val reducer: NavigationReducer = NestedNavigationReducer(MultiReducer(CustomModoReducer()))

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