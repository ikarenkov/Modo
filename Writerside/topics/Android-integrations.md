# Android Integrations

Modo provides Android integrations with ViewModel and Lifecycle support through `LocalLifecycleOwner`, `LocalViewModelStoreOwner`,
and `LocalSavedStateRegistryOwner`. This allows you to use functions like `viewModel` to obtain Android `ViewModel`.

> [Sample code](%github_code_url%sample/src/main/java/com/github/terrakok/modo/sample/screens/viewmodel/AndroidViewModelSampleScreen.kt) demonstrating
> Android integration inside a Modo Screen.

## Lifecycle

Since Modo provides Lifecycle support, let's take a look at what specific lifecycle events mean in the context of Screen Lifecycle:

* `Lifecycle.Event.ON_CREATE` - called once per screen when the screen is created.
* `Lifecycle.Event.ON_START` and `Lifecycle.Event.ON_RESUME` - dispatched when `Screen.Content` is composed for the first time.
* `Lifecycle.Event.ON_PAUSE` and `Lifecycle.Event.ON_STOP` - dispatched when `Screen.Content` leaves the composition.
* `Lifecycle.Event.ON_DESTROY` - called once per screen when it is removed from the navigation graph.

For correctly handling `Lifecycle.Event` from your `Screen.Content`, we recommend using the built-in [screen effects](Screen-effects.md).
The `LifecycleScreenEffect` is a convenient way to subscribe to a screen's Lifecycle.

```kotlin
LifecycleScreenEffect {
    LifecycleEventObserver { _, event ->
        logcat { "Lifecycle event $event." }
    }
}
```

> If you use `LaunchedEffect` or `DisposableEffect` to subscribe to the screen's Lifecycle, you won't receive `Lifecycle.Event.ON_DESTROY` because
> this event happens after the screen leaves the composition.

{ style="warning" }

## ViewModel

You can use functions provided by Jetpack Compose to get a `ViewModel`.

<include from="snippets.topic" element-id="under_develop_note"/>