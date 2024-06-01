class ModoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Column {
                    val rootScreen = rememberRootScreen {
                        SampleStack(MainScreen(screenIndex = 1, canOpenFragment = true))
                    }
                    rootScreen.Content(modifier = Modifier.fillMaxSize())
                }
            }
        }
}