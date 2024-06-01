package com.github.terrakok.modo.sample.quickstart

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.animation.ScreenTransition
import com.github.terrakok.modo.animation.StackTransitionType
import com.github.terrakok.modo.animation.calculateStackTransitionType
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.util.getActivity
import kotlinx.parcelize.Parcelize

@Parcelize
class QuickStartStackScreen(
    private val navModel: StackNavModel
) : StackScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier = modifier) {
            TopScreenContent(
                Modifier
                    .background(Color.Cyan)
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(32.dp))
                    .fillMaxSize()
                    .background(Color.White)
            ) { modifier ->
                ScreenTransition(
                    modifier = modifier,
                    transitionSpec = {
                        val screenTransitionType = calculateStackTransitionType()
                        when (screenTransitionType) {
                            StackTransitionType.Push -> {
                                slideInHorizontally(initialOffsetX = { it }) togetherWith
                                    slideOutHorizontally(targetOffsetX = { -it })
                            }
                            StackTransitionType.Pop -> {
                                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                    slideOutHorizontally(targetOffsetX = { it })
                            }
                            StackTransitionType.Replace, StackTransitionType.Idle -> fadeIn() togetherWith fadeOut()
                        }
                    }
                )
            }
            val context = LocalContext.current
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                onClick = { context.getActivity()?.finish() }
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Close),
                    contentDescription = "Close quick start activity"
                )
            }
        }
    }

}