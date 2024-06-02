class ModoManualIntegrationActivity : AppCompatActivity() {

    private var rootScreen: RootScreen<StackScreen>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rootScreen = Modo.getOrCreateRootScreen(savedInstanceState, rootScreen) {
            SampleStack(MainScreen(1))
        }
        setContent {
            ActivityContent {
                rootScreen?.Content(Modifier.fillMaxSize())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Modo.save(outState, rootScreen)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            Modo.onRootScreenFinished(rootScreen)
        }
    }

}