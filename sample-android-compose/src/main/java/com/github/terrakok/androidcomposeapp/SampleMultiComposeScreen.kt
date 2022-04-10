package com.github.terrakok.androidcomposeapp

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.github.terrakok.modo.MultiScreenState
import com.github.terrakok.modo.android.compose.MultiComposeScreen
import com.github.terrakok.modo.android.compose.MultiScreenStateParceler
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import com.github.terrakok.modo.selectStack
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<MultiScreenState, MultiScreenStateParceler>
class SampleMultiComposeScreen(
    override var saveableMultiScreenState: MultiScreenState,
    override val screenKey: String = uniqueScreenKey
) : MultiComposeScreen(saveableMultiScreenState, "SampleMultiComposeScreen"), Parcelable {

    @IgnoredOnParcel
    val modo = App.INSTANCE.modo

    @Composable
    override fun Content(innerContent: @Composable () -> Unit) {
        Column {
            Box(Modifier.weight(1f)) {
                innerContent()
            }
            BottomNavigation(multiScreenState.stacks.size, multiScreenState.selectedStack) { modo.selectStack(it) }
        }
    }

}

@Composable
fun BottomNavigation(stacksSize: Int, selectedStack: Int, onClick: (Int) -> Unit) {
    val items = sequenceOf(
        Icons.Default.Home,
        Icons.Default.Phone,
        Icons.Default.Star,
        Icons.Default.Person,
    )
    androidx.compose.material.BottomNavigation {
        items.take(stacksSize).forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item, contentDescription = "") },
                label = { Text(text = index.toString(), fontSize = 9.sp) },
                alwaysShowLabel = true,
                selected = index == selectedStack,
                onClick = { onClick(index) }
            )
        }
    }
}
