@Parcelize
class SampleScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        Text(
            text = "Hello, Modo! Screen №$screenIndex",
            modifier = modifier
        )
    }
}