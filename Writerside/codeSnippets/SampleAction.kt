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

sealed interface SampleAction : NavigationAction<SampleState> {
    class Remove : SampleAction
    class CreateScreen : SampleAction
}

@Parcelize
internal class RemovableItemContainerScreen(
    private val navModel: NavModel<RemovableItemContainerState, RemovableItemContainerAction> = NavModel(
        RemovableItemContainerState(
            NestedScreen(canBeRemoved = false),
            NestedScreen(canBeRemoved = false),
            NestedScreen(canBeRemoved = true),
            NestedScreen(canBeRemoved = false),
        )
    )
) : ContainerScreen<RemovableItemContainerState, RemovableItemContainerAction>(navModel) {

    override val reducer: NavigationReducer<RemovableItemContainerState, RemovableItemContainerAction> = NavigationReducer<RemovableItemContainerState, RemovableItemContainerAction> { action, state ->
        when (action) {
            is RemovableItemContainerAction.Remove -> {
                state.copy(screen3 = null)
            }
            is RemovableItemContainerAction.CreateScreen -> {
                state.copy(screen3 = NestedScreen(canBeRemoved = true))
            }
        }

    }

}
