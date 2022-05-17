package modo.sample.compose.feature.nestedNavigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.NestedNavigationReducer
import com.github.terrakok.modo.android.compose.NavigationStateParceler
import com.github.terrakok.modo.android.compose.NestedNavigationComposeScreen
import com.github.terrakok.modo.android.compose.uniqueScreenKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import modo.sample.compose.navigation.core.CustomModoReducer

@Parcelize
@TypeParceler<NavigationState, NavigationStateParceler>
class SampleNestedNavigationScreen(
    private val i: Int,
    override var saveableWreppeeState: NavigationState,
    override val screenKey: String = uniqueScreenKey
) : NestedNavigationComposeScreen("SampleWrapperScreen", saveableWreppeeState) {

    @IgnoredOnParcel
    override val reducer: NavigationReducer = NestedNavigationReducer(MultiReducer(CustomModoReducer()))

    @Composable
    override fun Content(innerContent: @Composable () -> Unit) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Nested navigation container $i",
                style = MaterialTheme.typography.h3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .border(4.dp, Color.Cyan, RoundedCornerShape(6.dp))
            ) {
                innerContent()
            }
        }
    }
}