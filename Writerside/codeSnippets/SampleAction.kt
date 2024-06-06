fun interface SampleAction : ReducerAction<SampleState> {
    class Remove : SampleAction {
        override fun reduce(oldState: SampleState): SampleState =
            oldState.copy(screen3 = null)
    }
    class CreateScreen : SampleAction {
        override fun reduce(oldState: SampleState): SampleState =
            oldState.copy(screen3 = NestedScreen(canBeRemoved = true))
    }
}