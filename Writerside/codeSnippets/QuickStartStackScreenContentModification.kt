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