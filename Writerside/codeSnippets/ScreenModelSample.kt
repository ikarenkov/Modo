@Parcelize
class ScreenModelSampleScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val screenModel = rememberScreenModel {
            SampleScreenModel()
        }
        Column(Modifier.fillMaxSize()) {
            Text(text = screenModel.state.intValue.toString())
        }
    }

}

private class SampleScreenModel : ScreenModel {

    val state = mutableIntStateOf(0)

    init {
        coroutineScope.launch {
            while (isActive) {
                delay(COUNTER_DELAY_MS)
                state.intValue++
            }
        }
    }

}