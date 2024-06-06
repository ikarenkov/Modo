# Android integrations

Modo provides android integrations of ViewModel and Lifecycle support by providing `LocalLifecycleOwner`, `LocalViewModelStoreOwner`
and `LocalSavedStateRegistryOwner`. So you can use functions like `viewModel` to obtain android `ViewModel`.

> [Sample code](%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/viewmodel/AndroidViewModelSampleScreen.kt) of usage
> android integration inside Modo Screen.

## Lifecycle

Since Modo provides LifeCycle support, let's take a look what specific lifecycle event means in the case of Screen Lifecycle:

* `Lifecycle.Event.ON_CREATE` - called once per a screen when screen is created.
* `Lifecycle.Event.ON_START` and `Lifecycle.Event.ON_RESUME` - dispatched when `Screen.Content` comes to composition by first time.
* `Lifecycle.Event.ON_PAUSE` and `Lifecycle.Event.ON_STOP` - dispatched when `Screen.Content` leaves composition.
* `Lifecycle.Event.ON_DESTROY` - called once per a `Screen` when it is removed from navigation graph.

For correct handling `Lifecycle.Event` from your `Screen.Content` we recommend to use build-in [screen effects](Screen-effects.md). There is
convenient `LifecycleScreenEffect` that subscribes to Screen's Lifecycle.

```Kotlin
LifecycleScreenEffect {
    LifecycleEventObserver { _, event ->
        logcat { "Lifecycle event $event." }
    }
}
```

> If you use `LaunchedEffect` or `DisposableEffect` to subscribe to the `Screen`'s Lifecycle, you won't receive `Lifecycle.Event.ON_DESTROY` because
> this event happens after leaving the composition.

{ style="warning" }

## ViewModel

You can use functions provided by Jetpack Compose to get `ViewModel`.

<include from="snippets.topic" element-id="under_develop_note"/>