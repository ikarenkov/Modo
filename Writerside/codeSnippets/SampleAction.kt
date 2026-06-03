fun interface SampleReducer : NavigationReducer<SampleState>

object SampleReducers {
    val Remove = SampleReducer { oldState ->
        oldState.copy(screen3 = null)
    }

    val CreateScreen = SampleReducer { oldState ->
        oldState.copy(screen3 = NestedScreen(canBeRemoved = true))
    }
}
