// You need to use Parcelize plugin to generate Parcelable implementation for process death survavial
@Parcelize
class SampleScreen(
    // You can pass argiment as a constructor parameter
    private val screenIndex: Int,
    // You need to generate a unique screen key using special function
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        // Taking a nearest stack navigation container
        val stackNavigation = LocalStackNavigation.current
        SampleScreenContent(
            modifier = modifier,
            screenIndex = screenIndex,
            openNextScreen = { stackNavigation.forward(SampleScreen(screenIndex + 1)) },
        )
    }
}

@Composable
private fun SampleScreenContent(
    modifier: Modifier,
    screenIndex: Int,
    openNextScreen: () -> Unit,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(text = "Hello, Modo! Screen №$screenIndex")
        Button(
            onClick = openNextScreen
        ) {
            Text(text = "Next screen")
        }
    }
}