@Parcelize
class QuickStartStackScreen(
    private val navModel: StackNavModel
) : StackScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        TopScreenContent(modifier) { modifier ->
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
    }

}