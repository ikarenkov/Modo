class QuickStartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Remember root screen using rememberSeaveable under the hood.
            val rootScreen = rememberRootScreen {
                DefaultStackScreen(StackNavModel(SampleScreen(screenIndex = 1)))
            }
            rootScreen.Content(modifier = Modifier.fillMaxSize())
        }
    }

}
